package top.itfinally.mybatis.jpa.context;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ExtendedBeanInfoFactory;
import top.itfinally.mybatis.jpa.entity.AttributeMetadata;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.EntityMetadataToken;
import top.itfinally.mybatis.jpa.entity.ForeignAttributeMetadata;
import top.itfinally.mybatis.jpa.exception.DuplicatePrimaryKeyException;
import top.itfinally.mybatis.jpa.exception.MissingPrimaryKeyException;
import top.itfinally.mybatis.jpa.exception.NoSuchActualTypeException;

import javax.persistence.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/24       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class MetadataFactory {
    private static final ExtendedBeanInfoFactory beanInfoFactory = new ExtendedBeanInfoFactory();
    private static final Logger logger = LoggerFactory.getLogger( MetadataFactory.class );
    private static final Cache<Class<?>, EntityMetadata> metadataBuffer = CacheBuilder.newBuilder()
            .concurrencyLevel( Runtime.getRuntime().availableProcessors() )
            .maximumSize( 10240 )
            .initialCapacity( 64 )
            .build();

    private static final Set<ForeignAttributeMetadata> tokens = Sets.newConcurrentHashSet();
    private static final ConcurrentMap<String, EntityMetadata> tableMapping = new ConcurrentHashMap<>();

    private MetadataFactory() {
    }

    public static void hittingMetadata( List<Class<?>> entityClasses ) {
        for ( Class<?> clazz : entityClasses ) {
            metadataBuffer.put( clazz, buildEntity( clazz ) );
        }

        if ( !tokens.isEmpty() ) {
            Iterator<ForeignAttributeMetadata> iterator = tokens.iterator();
            ForeignAttributeMetadata item;
            String token;

            while ( iterator.hasNext() ) {
                item = iterator.next();
                token = ( ( EntityMetadataToken ) item.getEntityMetadata() ).getToken();

                if ( !( item.getEntityMetadata() instanceof EntityMetadataToken && tableMapping.containsKey( token ) ) ) {
                    continue;
                }

                item.setEntityMetadata( tableMapping.get( ( ( EntityMetadataToken ) item.getEntityMetadata() ).getToken() ) );
                injectForeignAttribute( item, item.getEntityMetadata().getEntityClass() );

                iterator.remove();
            }
        }

        if ( !tokens.isEmpty() ) {
            List<String> names = new ArrayList<>();
            for ( ForeignAttributeMetadata item : tokens ) {
                names.add( String.format( "attribute %s of entity %s ( require table %s )",
                        item.getJavaName(), item.getField().getDeclaringClass().getName(),
                        ( ( EntityMetadataToken ) item.getEntityMetadata() ).getToken() ) );
            }

            throw new IllegalStateException( String.format( "Some entities without matches table has been found: ( %s )",
                    Joiner.on( "\n" ).join( names ) ) );
        }
    }

    public static EntityMetadata getMetadata( final Class<?> entityClass ) {
        try {
            return metadataBuffer.get( entityClass, new Callable<EntityMetadata>() {
                @Override
                public EntityMetadata call() {
                    return buildEntity( entityClass );
                }
            } );

        } catch ( ExecutionException e ) {
            logger.error( "Catch async task error", e.getCause() );
            throw new RuntimeException( "Failure to getting entity metadata", e.getCause() );
        }
    }

    private static EntityMetadata buildEntity( Class<?> entityClass ) {
        Table table = entityClass.getAnnotation( Table.class );
        if ( null == table ) {
            throw new IllegalStateException( String.format( "Missing annotation '@Table' on entity '%s'", entityClass.getName() ) );
        }

        if ( Strings.isNullOrEmpty( table.name() ) ) {
            throw new IllegalStateException( String.format( "Missing a table name on entity '%s'", entityClass.getName() ) );
        }

        EntityMetadata metadata = new EntityMetadata().setEntityClass( entityClass ).setTableName( table.name() );
        Map<Class<?>, ForeignAttributeMetadata> referenceMetadataMap = createAttributeAndInject( metadata, entityClass );

        // for table tokens
        tableMapping.put( table.name(), metadata );

        if ( null == metadata.getId() ) {
            throw new MissingPrimaryKeyException( String.format( "Missing a primary key on entity '%s'", entityClass.getName() ) );
        }

        if ( !referenceMetadataMap.isEmpty() ) {
            Deque<Class<?>> entityClasses = new ArrayDeque<>( referenceMetadataMap.keySet() );
            buildEntityAndInjectToReferenceMetadata( entityClasses.pop(), entityClasses, referenceMetadataMap );
        }

        return metadata;
    }

    private static Map<Class<?>, ForeignAttributeMetadata> buildEntityAndInjectToReferenceMetadata(
            Class<?> target, Deque<Class<?>> entityClasses, Map<Class<?>, ForeignAttributeMetadata> referenceMetadataMap ) {

        ForeignAttributeMetadata foreignAttributeMetadata = referenceMetadataMap.get( target );

        foreignAttributeMetadata.setEntityMetadata( buildEntity( target ) );

        // inject foreign attribute metadata
        injectForeignAttribute( foreignAttributeMetadata, target );

        return entityClasses.isEmpty() ? referenceMetadataMap : buildEntityAndInjectToReferenceMetadata( entityClasses.pop(), entityClasses, referenceMetadataMap );
    }

    private static void injectForeignAttribute( ForeignAttributeMetadata foreignAttributeMetadata, Class<?> target ) {
        String referenceAttributeName = foreignAttributeMetadata.getReferenceAttributeMetadata().getJavaName();

        foreignAttributeMetadata.setReferenceAttributeMetadata( null );

        List<AttributeMetadata> columns = new ArrayList<>();
        columns.addAll( foreignAttributeMetadata.getEntityMetadata().getColumns() );
        columns.addAll( foreignAttributeMetadata.getEntityMetadata().getReferenceColumns() );

        for ( AttributeMetadata item : columns ) {
            if ( item.getJavaName().equals( referenceAttributeName ) ) {
                foreignAttributeMetadata.setReferenceAttributeMetadata( item );
                break;
            }
        }

        if ( null == foreignAttributeMetadata.getReferenceAttributeMetadata() ) {
            if ( Strings.isNullOrEmpty( referenceAttributeName ) ) {
                throw new IllegalStateException( "The foreign attribute is empty ( require attribute 'referencedColumnName' of @JoinColumn )" );
            }

            throw new IllegalStateException( String.format( "The foreign attribute '%s' is not found at entity '%s'",
                    referenceAttributeName, target.getName() ) );
        }
    }

    private static Map<Class<?>, ForeignAttributeMetadata> createAttributeAndInject( EntityMetadata metadata, Class<?> entityClass ) {
        List<ForeignAttributeMetadata> foreignAttributeMetadataList = metadata.setReferenceColumns( new ArrayList<ForeignAttributeMetadata>() ).getReferenceColumns();
        List<AttributeMetadata> attributeMetadataList = metadata.setColumns( new ArrayList<AttributeMetadata>() ).getColumns();
        BeanInfo beanInfo;

        try {
            beanInfo = beanInfoFactory.getBeanInfo( entityClass );

        } catch ( IntrospectionException e ) {
            throw new RuntimeException( String.format( "Failure to getting getter/setter of entity '%s'", entityClass.getName() ), e );
        }

        if ( null == beanInfo ) {
            throw new IllegalStateException( String.format( "maybe missing getter/setter method in entity class '%s' ?", entityClass.getName() ) );
        }

        Field field;
        ColumnAnnotation column;
        AttributeMetadata attributeMetadata;
        Map<Class<?>, ForeignAttributeMetadata> relationEntityClasses = new HashMap<>();

        Class<?> targetEntity;
        Relation relationship;
        ForeignAttributeMetadata foreignAttributeMetadata;

        for ( PropertyDescriptor pd : beanInfo.getPropertyDescriptors() ) {
            if ( "class".equals( pd.getDisplayName() ) ) {
                continue;
            }

            field = getDeclareField( entityClass, pd.getName(), boolean.class == pd.getPropertyType() );
            if ( null == field ) {
                throw new IllegalStateException( String.format( "No such field '%s' on entity '%s'", pd.getName(), entityClass.getName() ) );
            }

            relationship = new Relation( field, pd );
            column = new ColumnAnnotation( field, pd );

            if ( relationship.hasRelationship() ) {
                if ( !column.isJoinColumn() ) {
                    throw new IllegalStateException( "Should be use '@JoinColumn' when attribute is marked " +
                            "with relationship annotation like '@OneToOne'" );
                }

                foreignAttributeMetadata = createAttributeMetadata( new ForeignAttributeMetadata(), column, field, pd )
                        .setLazy( relationship.isLazy() );

                targetEntity = relationship.getTargetEntity();
                if ( null == targetEntity || void.class == targetEntity || Void.class == targetEntity ) {
                    // see attribute targetEntity of '@ManyToMany, @OneToMany, @ManyToOne, @OneToOne'

                    Class<?> returnType = field.getType();

                    // Here to analysis relationship between entities
                    // single table query and one to many relationship query( including one to one relationship ).
                    //
                    // It hard to analysis many to many relationship and mapping it by code, because it is logic in human
                    // especially, seem there are no way to generate the right sql to do many to many relationship query
                    // This is the reason of why mybatis-jpa unsupported MANY TO MANY relationship query
                    if ( Collection.class.isAssignableFrom( returnType ) ) {
                        foreignAttributeMetadata.setCollection( true );
                        targetEntity = getActualType( field );

                        if ( Map.class.isAssignableFrom( targetEntity ) ) {
                            foreignAttributeMetadata.setActualType( targetEntity );
                            targetEntity = null;

                        } else if ( targetEntity.getAnnotation( Table.class ) != null ) {
                            foreignAttributeMetadata.setActualType( targetEntity );

                        } else {
                            throw new IllegalArgumentException( String.format( "The generic type of %s must be an entity " +
                                    "what marked with '@Table' or type Map", returnType.getSimpleName() ) );
                        }

                    } else if ( Map.class.isAssignableFrom( returnType ) ) {
                        foreignAttributeMetadata.setCollection( false ).setActualType( returnType );
                        targetEntity = null;

                    } else if ( returnType.getAnnotation( Table.class ) != null ) {
                        foreignAttributeMetadata.setCollection( false ).setActualType( returnType );
                        targetEntity = returnType;

                    } else {
                        throw new IllegalStateException( "The foreign attribute have an unknown java type, " +
                                "only accept type of 'Collection', 'Map' or any Entity marked with '@Table' annotation" );
                    }
                }

                if ( targetEntity != null ) {
                    foreignAttributeMetadata.setReferenceAttributeMetadata( new AttributeMetadata()
                            .setJavaName( column.joinColumn.referencedColumnName() ) );

                    relationEntityClasses.put( targetEntity, foreignAttributeMetadata );

                } else if ( !Strings.isNullOrEmpty( column.joinColumn.table() ) ) {
                    foreignAttributeMetadata.setReferenceAttributeMetadata( new AttributeMetadata()
                            .setJavaName( column.joinColumn.referencedColumnName() ) )

                    .setEntityMetadata( new EntityMetadataToken().setToken( column.joinColumn.table() ) );

                    tokens.add( foreignAttributeMetadata );
                }

                foreignAttributeMetadataList.add( foreignAttributeMetadata );
                continue;
            }

            attributeMetadata = createAttributeMetadata( new AttributeMetadata(), column, field, pd );
            attributeMetadataList.add( attributeMetadata );

            if ( attributeMetadata.isPrimary() ) {
                if ( metadata.getId() != null ) {
                    throw new DuplicatePrimaryKeyException( String.format(
                            "Duplicate primary key on entity '%s'", entityClass.getName() ) );
                }

                metadata.setId( attributeMetadata );
            }
        }

        return relationEntityClasses;
    }

    @SuppressWarnings( "unchecked" )
    private static <E extends AttributeMetadata> E createAttributeMetadata(
            E metadata, ColumnAnnotation column, Field field, PropertyDescriptor pd ) {

        return ( E ) metadata.setField( field )
                .setJavaName( field.getName() )
                .setNullable( column.isNullable() )
                .setJdbcName( column.getJdbcName() )
                .setReadMethod( pd.getReadMethod() )
                .setWriteMethod( pd.getWriteMethod() )
                .setPrimary( getJpaAnnotation( Id.class, field, pd ) != null );
    }

    private static Field getDeclareField( Class<?> clazz, String field, boolean isBoolean ) {
        try {
            Field target = clazz.getDeclaredField( field );

            if ( isBoolean && null == target ) {
                char[] chars = field.toCharArray();
                chars[ 0 ] = Character.toUpperCase( chars[ 0 ] );
                target = clazz.getDeclaredField( String.format( "is%s", new String( chars ) ) );
            }

            if ( target != null ) {
                return target;
            }

        } catch ( NoSuchFieldException ignore ) {
        }

        Class<?> superCls = clazz.getSuperclass();
        return superCls != null && superCls != Object.class ? getDeclareField( superCls, field, isBoolean ) : null;
    }

    private static Class<?> getActualType( Field field ) {
        Type type = field.getGenericType();
        if ( !( type instanceof ParameterizedType ) ) {
            throw new NoSuchActualTypeException( "No actual type found from generic type of Collection " +
                    String.format( "( maybe you missing generic type, expect '%s<ActualType>' but got '%s' )",
                            field.getType().getSimpleName(), field.getType().getSimpleName() ) );
        }

        return ( Class<?> ) ( ( ParameterizedType ) type ).getActualTypeArguments()[ 0 ];
    }

    private static <T extends Annotation> T getJpaAnnotation( Class<T> clazz, Field field, PropertyDescriptor pd ) {
        T column;
        Method method;

        method = pd.getReadMethod();
        if ( method != null ) {
            column = method.getAnnotation( clazz );

            if ( column != null ) {
                return column;
            }
        }

        method = pd.getWriteMethod();
        if ( method != null ) {
            column = method.getAnnotation( clazz );

            if ( column != null ) {
                return column;
            }
        }

        column = field.getAnnotation( clazz );
        if ( column != null ) {
            return column;
        }

        return null;
    }

    private static class ColumnAnnotation {
        private final Column column;
        private final JoinColumn joinColumn;

        private ColumnAnnotation( Field field, PropertyDescriptor pd ) {
            column = getJpaAnnotation( Column.class, field, pd );
            joinColumn = getJpaAnnotation( JoinColumn.class, field, pd );

            if ( null == column && null == joinColumn ) {
                throw new IllegalStateException( String.format( "Missing annotation '@Column' or '@JoinColumn' on entity field '%s'", field.getName() ) );
            }
        }

        private String getJdbcName() {
            return joinColumn != null ? joinColumn.name() : column.name();
        }

        private boolean isNullable() {
            return joinColumn != null ? joinColumn.nullable() : column.nullable();
        }

        private boolean isJoinColumn() {
            return joinColumn != null;
        }
    }

    private static class Relation {
        private final OneToOne oneToOne;
        private final OneToMany oneToMany;
        private final ManyToOne manyToOne;

        private Relation( Field field, PropertyDescriptor pd ) {
            if ( getJpaAnnotation( ManyToMany.class, field, pd ) != null ) {
                throw new UnsupportedOperationException( "Mybatis-jpa unsupported many to many relationship." );
            }

            oneToOne = getJpaAnnotation( OneToOne.class, field, pd );
            oneToMany = getJpaAnnotation( OneToMany.class, field, pd );
            manyToOne = getJpaAnnotation( ManyToOne.class, field, pd );

            int relationshipCount = 0;

            if ( oneToOne != null ) {
                relationshipCount += 1;
            }

            if ( oneToMany != null ) {
                relationshipCount += 1;
            }

            if ( manyToOne != null ) {
                relationshipCount += 1;
            }

            if ( relationshipCount > 1 ) {
                throw new IllegalStateException( String.format( "There multi-relationship on attribute '%s' of entity class '%s'",
                        field.getName(), field.getDeclaringClass().getName() ) );
            }
        }

        private boolean isLazy() {
            if ( oneToOne != null ) {
                return oneToOne.fetch() == FetchType.LAZY;
            }

            if ( oneToMany != null ) {
                return oneToMany.fetch() == FetchType.LAZY;
            }

            if ( manyToOne != null ) {
                return manyToOne.fetch() == FetchType.LAZY;
            }

            return false;
        }

        private Class<?> getTargetEntity() {
            if ( oneToOne != null ) {
                return oneToOne.targetEntity();
            }

            if ( oneToMany != null ) {
                return oneToMany.targetEntity();
            }

            if ( manyToOne != null ) {
                return manyToOne.targetEntity();
            }

            return null;
        }

        private boolean hasRelationship() {
            return oneToOne != null || oneToMany != null || manyToOne != null;
        }
    }
}
