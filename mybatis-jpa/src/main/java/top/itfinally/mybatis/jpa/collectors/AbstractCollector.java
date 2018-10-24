package top.itfinally.mybatis.jpa.collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import top.itfinally.mybatis.jpa.context.MetadataFactory;
import top.itfinally.mybatis.jpa.criteria.*;
import top.itfinally.mybatis.jpa.criteria.path.RootImpl;
import top.itfinally.mybatis.jpa.criteria.predicate.CompoundPredicate;
import top.itfinally.mybatis.jpa.criteria.query.AbstractQuery;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaSubQueryImpl;
import top.itfinally.mybatis.jpa.criteria.query.SubQuery;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.JoinMetadata;
import top.itfinally.mybatis.jpa.utils.TypeMatcher;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.Table;
import javax.persistence.criteria.JoinType;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/16       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@NotThreadSafe
public abstract class AbstractCollector implements Writable {
    private final AtomicInteger version = new AtomicInteger( 0 );
    protected final CriteriaBuilder criteriaBuilder;
    protected final AbstractQuery<?> parent;
    protected final AbstractQuery<?> owner;
    protected final boolean subQuery;

    // From clause
    protected OrderHashMap<Root<?>> roots = new OrderHashMap<>();

    // Join clause
    protected final OrderHashMap<JoinMetadata> joinMetadata = new OrderHashMap<>();
    protected final List<Predicate> joinExpression = new ArrayList<>();

    // Where clause and sub queries
    protected final List<Expression<?>> expressions = new ArrayList<>();
    protected final List<SubQuery<?>> subQueries = new ArrayList<>();

    protected AbstractCollector( CriteriaBuilder criteriaBuilder, AbstractQuery<?> owner ) {
        this.criteriaBuilder = criteriaBuilder;
        this.parent = null;
        this.owner = owner;

        this.subQuery = owner instanceof SubQuery;
    }

    protected AbstractCollector( CriteriaBuilder criteriaBuilder, AbstractQuery<?> parent, AbstractQuery<?> owner ) {
        this.criteriaBuilder = criteriaBuilder;
        this.parent = parent;
        this.owner = owner;

        this.subQuery = owner instanceof SubQuery;
    }


    // Operations
    //
    //

    public void addCondition( final Collection<Expression<Boolean>> predicates ) {
        if ( TypeMatcher.hasNullValueInCollection( predicates ) ) {
            throw new NullPointerException( "There are have null value inside the given collection" );
        }

        concurrentChecking( new Runnable() {
            @Override
            public void run() {
                expressions.addAll( predicates );
            }
        } );
    }

    public void addJoiner( final JoinMetadata metadata ) {
        concurrentChecking( new Runnable() {
            @Override
            public void run() {
                joinMetadata.orderPut( metadata.getClassName(), metadata );
            }
        } );
    }

    public void addJoinCondition( final List<Predicate> restrictions ) {
        if ( TypeMatcher.hasNullValueInCollection( restrictions ) ) {
            throw new NullPointerException( "There are have null value inside the given collection" );
        }

        concurrentChecking( new Runnable() {
            @Override
            public void run() {
                joinExpression.addAll( restrictions );
            }
        } );
    }


    // Factory
    //
    //

    @SuppressWarnings( "unchecked" )
    public <T> Root<T> from( final Class<T> entityClass ) {
        if ( entityClass.getAnnotation( Table.class ) == null ) {
            throw new IllegalArgumentException( "Entity class must be marked with '@Table'" );
        }

        return concurrentChecking( new Supplier<Root<T>>() {
            @Override
            public Root<T> get() {
                EntityMetadata entityMetadata = MetadataFactory.getMetadata( entityClass );
                String className = entityMetadata.getEntityClass().getName();

                if ( !roots.containsKey( className ) ) {
                    roots.orderPut( className, new RootImpl<>( criteriaBuilder,
                            AbstractCollector.this, entityMetadata ) );
                }

                return ( Root<T> ) roots.get( className ).getValue();
            }
        } );
    }

    public <Entity> SubQuery<Entity> subQuery() {
        return concurrentChecking( new Supplier<SubQuery<Entity>>() {
            @Override
            public SubQuery<Entity> get() {
                SubQuery<Entity> subQuery = new CriteriaSubQueryImpl<>( criteriaBuilder, owner );

                subQueries.add( subQuery );

                return subQuery;
            }
        } );
    }


    // Builder
    //
    //


    protected String completeRoots( ParameterBus parameters ) {
        OrderHashMap.OrderWrapper<JoinMetadata> item;
        EntityMetadata metadata;

        List<Root<?>> unallocatedRoots = new ArrayList<>();
        String[] items = new String[ roots.size() ];

        // extract
        for ( Root<?> root : roots.orderValues() ) {
            metadata = root.getModel().getEntityMetadata();

            item = joinMetadata.get( metadata.getEntityClass().getName() );
            if ( null == item ) {
                unallocatedRoots.add( root );
                continue;
            }

            items[ item.index ] = String.format( "%s %s %s", renderJoinType( item.value.getType(), item.value.getClassName() ),
                    metadata.getTableName(), ( ( Writable ) root ).toFormatString( parameters ) );
        }

        // 'from' clause
        List<String> tables = new ArrayList<>();
        for ( Root<?> root : unallocatedRoots ) {
            metadata = root.getModel().getEntityMetadata();
            tables.add( String.format( "%s %s", metadata.getTableName(), ( ( Writable ) root ).toFormatString( parameters ) ) );
        }

        if ( tables.isEmpty() ) {
            throw new IllegalStateException( "There are no table selected on criteria query" );
        }

        // 'join' clause
        List<String> joiners = new ArrayList<>();
        for ( String joiner : items ) {
            if ( Strings.isNullOrEmpty( joiner ) ) {
                continue;
            }

            joiners.add( joiner );
        }

        // 'on' clause
        List<String> expressions = new ArrayList<>();
        for ( Predicate expression : joinExpression ) {
            expressions.add( ( ( Writable ) expression ).toFormatString( parameters ) );
        }

        // A, B, C left join D inner join E right join F on A1 = B2 and C3 = D4
        StringBuilder fromClause = new StringBuilder( Joiner.on( ", " ).join( tables ) );
        if ( !joiners.isEmpty() ) {
            fromClause.append( " " ).append( Joiner.on( " " ).join( joiners ) );

            if ( !expressions.isEmpty() ) {
                fromClause.append( " on " ).append( Joiner.on( " and " ).join( expressions ) );
            }
        }

        return fromClause.toString();
    }

    protected String completeConditions( ParameterBus parameters ) {
        List<String> conditions = new ArrayList<>();

        if ( 1 == expressions.size() && expressions.get( 0 ) instanceof CompoundPredicate ) {
            // There are no need to use brackets
            String content = ( ( CompoundPredicate ) expressions.get( 0 ) ).toFormatString( parameters ).trim();
            return content.replaceFirst( "^\\(", "" ).replaceFirst( "\\)$", "" );

        } else {
            for ( Expression<?> item : expressions ) {
                conditions.add( ( ( Writable ) item ).toFormatString( parameters ) );
            }
        }

        return conditions.isEmpty() ? "" : Joiner.on( " and " ).join( conditions );
    }


    // Helper
    //
    //

    protected <V> V concurrentChecking( Supplier<V> task ) {
        int currentVersion = version.get();

        V result = task.get();

        if ( !version.compareAndSet( currentVersion, currentVersion + 1 ) ) {
            throw new ConcurrentModificationException( "Criteria object is not supported on concurrent" );
        }

        return result;
    }

    protected void concurrentChecking( Runnable task ) {
        int currentVersion = version.get();

        task.run();

        if ( !version.compareAndSet( currentVersion, currentVersion + 1 ) ) {
            throw new ConcurrentModificationException( "Criteria object is not supported on concurrent" );
        }
    }

    protected static class OrderHashMap<V> extends HashMap<String, OrderHashMap.OrderWrapper<V>>
            implements Map<String, OrderHashMap.OrderWrapper<V>> {

        private final AtomicInteger counter = new AtomicInteger();

        V orderPut( String key, V value ) {
            OrderWrapper<V> orderWrapper = put( key, new OrderWrapper<>( counter.getAndIncrement(), value ) );
            return null == orderWrapper ? null : orderWrapper.value;
        }

        List<V> orderValues() {
            List<OrderWrapper<V>> wrappers = new ArrayList<>( values() );

            Collections.sort( wrappers, new Comparator<OrderWrapper<V>>() {
                @Override
                public int compare( OrderWrapper<V> o1, OrderWrapper<V> o2 ) {
                    return Integer.compare( o1.index, o2.index );
                }
            } );

            List<V> values = new ArrayList<>( wrappers.size() );
            for ( OrderWrapper<V> item : wrappers ) {
                values.add( item.value );
            }

            return values;
        }

        public static class OrderWrapper<V> {
            private final int index;
            private final V value;

            private OrderWrapper( int index, V value ) {
                Objects.requireNonNull( value );

                this.index = index;
                this.value = value;
            }

            public V getValue() {
                return value;
            }

            @Override
            @SuppressWarnings( "all" )
            public boolean equals( Object o ) {
                return value.equals( o );
            }

            @Override
            public int hashCode() {
                return value.hashCode();
            }
        }
    }

    private String renderJoinType( JoinType type, String className ) {
        switch ( type ) {
            case LEFT:
                return "left join";

            case RIGHT:
                return "right join";

            case INNER:
                return "inner join";

            default:
                throw new IllegalStateException( "Unknown joining type with class " + className );
        }
    }
}
