package top.itfinally.mybatis.jpa.criteria.query;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import top.itfinally.mybatis.jpa.context.MetadataFactory;
import top.itfinally.mybatis.jpa.criteria.*;
import top.itfinally.mybatis.jpa.criteria.expression.AttributePath;
import top.itfinally.mybatis.jpa.criteria.path.RootImpl;
import top.itfinally.mybatis.jpa.criteria.render.OrderConcurrentHashMap;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.JoinMetadata;

import javax.persistence.criteria.JoinType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/29       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class QueryCollector implements Writable {
    private static ConcurrentMap<Class<?>, EntityMetadata> entityMetadataBuffer = new ConcurrentHashMap<>( 32 );

    private final AtomicInteger version = new AtomicInteger( 0 );
    private final CriteriaBuilder criteriaBuilder;
    private final AbstractQuery<?> parentQuery;
    private final boolean subQuery;

    // expression
    private List<Reference<?>> selections = new ArrayList<>();

    // tables, unique
    private OrderHashMap<Root<?>> roots = new OrderHashMap<>();

    // joining
    private OrderHashMap<JoinMetadata> joinMetadata = new OrderHashMap<>();
    private List<Predicate> joinExpression = new ArrayList<>();

    private List<Expression<?>> expressions = new ArrayList<>();
    private List<SubQuery<?>> subQueries = new ArrayList<>();
    private List<Reference<?>> grouping = new ArrayList<>();

    public QueryCollector( CriteriaBuilder criteriaBuilder, AbstractQuery<?> parentQuery ) {
        this.criteriaBuilder = criteriaBuilder;
        this.parentQuery = parentQuery;

        this.subQuery = parentQuery instanceof SubQuery;
    }

    public void addSelection( final Collection<Reference<?>> selections ) {
        concurrentChecking( new Runnable() {
            @Override
            public void run() {
                QueryCollector.this.selections.addAll( selections );
            }
        } );
    }

    public void addCondition( final Collection<Expression<Boolean>> predicates ) {
        concurrentChecking( new Runnable() {
            @Override
            public void run() {
                expressions.addAll( predicates );
            }
        } );
    }

    public void addGrouping( final Collection<Reference<?>> paths ) {
        concurrentChecking( new Runnable() {
            @Override
            public void run() {
                grouping.addAll( paths );
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
        concurrentChecking( new Runnable() {
            @Override
            public void run() {
                joinExpression.addAll( restrictions );
            }
        } );
    }

    @SuppressWarnings( "unchecked" )
    public <T> Root<T> from( final Class<T> entityClass ) {
        return concurrentChecking( new Supplier<Root<T>>() {
            @Override
            public Root<T> get() {
                if ( !entityMetadataBuffer.containsKey( entityClass ) ) {
                    entityMetadataBuffer.putIfAbsent( entityClass, MetadataFactory.build( entityClass ) );
                }

                EntityMetadata entityMetadata = entityMetadataBuffer.get( entityClass );
                String className = entityMetadata.getEntityClass().getName();

                if ( !roots.containsKey( className ) ) {
                    roots.orderPut( className, new RootImpl<>( criteriaBuilder,
                            QueryCollector.this, entityMetadata ) );
                }

                return ( Root<T> ) roots.get( className ).getValue();
            }
        } );
    }

    public <Entity> SubQuery<Entity> subQuery( final Class<Entity> entityClass ) {
        return concurrentChecking( new Supplier<SubQuery<Entity>>() {
            @Override
            public SubQuery<Entity> get() {
                SubQuery<Entity> subQuery = new CriteriaSubQueryImpl<>( criteriaBuilder, entityClass, parentQuery );

                subQueries.add( subQuery );

                return subQuery;
            }
        } );
    }

    private <V> V concurrentChecking( Supplier<V> task ) {
        int currentVersion = version.get();

        V result = task.get();

        if ( !version.compareAndSet( currentVersion, currentVersion + 1 ) ) {
            throw new ConcurrentModificationException( "Criteria query is not supported on concurrent" );
        }

        return result;
    }

    private void concurrentChecking( Runnable task ) {
        int currentVersion = version.get();

        task.run();

        if ( !version.compareAndSet( currentVersion, currentVersion + 1 ) ) {
            throw new ConcurrentModificationException( "Criteria query is not supported on concurrent" );
        }
    }

    // sql builder

    @Override
    public String toFormatString( ParameterBus parameters ) {
        String selectionClause = completeSelections( parameters );
        String fromClause = completeRoots( parameters );
        String whereClause = completeConditions( parameters );

        StringBuilder sql = new StringBuilder( String.format( " select %s from %s ", selectionClause, fromClause ) );
        if ( !Strings.isNullOrEmpty( whereClause ) ) {
            sql.append( " where " ).append( whereClause );
        }

        return sql.toString();
    }

    private String completeSelections( ParameterBus parameters ) {
        Set<String> rootNames = new HashSet<>();
        List<String> selectionStrings = new ArrayList<>();

        if ( selections.isEmpty() ) {
            throw new IllegalStateException( "There are no columns to selected" );
        }

        // only roots
        for ( Reference<?> item : selections ) {
            if ( !( item instanceof RootImpl ) ) {
                continue;
            }

            rootNames.add( ( ( RootImpl<?> ) item ).getModel().getEntityMetadata().getEntityClass().getName() );
            selectionStrings.add( ( ( RootImpl<?> ) item ).toFormatString( parameters ) );
        }

        // only attributes
        String attributeString;
        for ( Reference<?> item : selections ) {
            if ( !( item instanceof AttributePath ) || rootNames.contains( ( ( AttributePath<?> ) item )
                    .getModel().getEntityMetadata().getEntityClass().getName() ) ) {

                continue;
            }

            attributeString = ( ( AttributePath<?> ) item ).toFormatString( parameters );
            if ( !Strings.isNullOrEmpty( item.getAlias() ) ) {
                attributeString += String.format( " as %s ", item.getAlias() );
            }

            selectionStrings.add( attributeString );
        }

        return Joiner.on( " , " ).join( selectionStrings );
    }

    private String completeRoots( ParameterBus parameters ) {
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

            items[ item.index ] = String.format( " %s %s %s ", renderJoinType( item.value.getType(), item.value.getClassName() ),
                    metadata.getTableName(), ( ( Writable ) root ).toFormatString( parameters ) );
        }

        // 'from' clause
        List<String> tables = new ArrayList<>();
        for ( Root<?> root : unallocatedRoots ) {
            metadata = root.getModel().getEntityMetadata();
            tables.add( String.format( " %s %s ", metadata.getTableName(), ( ( Writable ) root ).toFormatString( parameters ) ) );
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

        StringBuilder fromClause = new StringBuilder( Joiner.on( " , " ).join( tables ) );
        if ( !joiners.isEmpty() ) {
            fromClause.append( Joiner.on( " " ).join( joiners ) );

            if ( !expressions.isEmpty() ) {
                fromClause.append( " on " ).append( Joiner.on( " and " ).join( expressions ) );
            }
        }

        return fromClause.toString();
    }

    private String completeConditions( ParameterBus parameters ) {
        List<String> conditions = new ArrayList<>();

        for ( Expression<?> item : expressions ) {
            conditions.add( ( ( Writable ) item ).toFormatString( parameters ) );
        }

        return conditions.isEmpty() ? "" : Joiner.on( " and " ).join( conditions );
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

    private static class OrderHashMap<V> extends HashMap<String, OrderHashMap.OrderWrapper<V>>
            implements Map<String, OrderHashMap.OrderWrapper<V>> {

        private final AtomicInteger counter = new AtomicInteger();

        V orderPut( String key, V value ) {
            OrderHashMap.OrderWrapper<V> orderWrapper = put( key, new OrderHashMap.OrderWrapper<>( counter.getAndIncrement(), value ) );
            return null == orderWrapper ? null : orderWrapper.value;
        }

        List<V> orderValues() {
            List<OrderHashMap.OrderWrapper<V>> wrappers = new ArrayList<>( values() );

            Collections.sort( wrappers, new Comparator<OrderHashMap.OrderWrapper<V>>() {
                @Override
                public int compare( OrderHashMap.OrderWrapper<V> o1, OrderHashMap.OrderWrapper<V> o2 ) {
                    return Integer.compare( o1.index, o2.index );
                }
            } );

            List<V> values = new ArrayList<>( wrappers.size() );
            for ( OrderHashMap.OrderWrapper<V> item : wrappers ) {
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
}
