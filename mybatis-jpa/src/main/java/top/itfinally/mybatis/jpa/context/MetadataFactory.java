package top.itfinally.mybatis.jpa.context;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ExtendedBeanInfoFactory;
import top.itfinally.mybatis.jpa.entity.AttributeMetadata;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.ReferenceMetadata;
import top.itfinally.mybatis.jpa.exception.DuplicatePrimaryKeyException;
import top.itfinally.mybatis.jpa.exception.MissingPrimaryKeyException;

import javax.persistence.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    private static final ConcurrentMap<Class<?>, FutureTask<EntityMetadata>> metadataCache = new ConcurrentHashMap<>();
    private static final ExtendedBeanInfoFactory beanInfoFactory = new ExtendedBeanInfoFactory();
    private static final Logger logger = LoggerFactory.getLogger( MetadataFactory.class );

    private MetadataFactory() {
    }

    public static EntityMetadata build( Class<?> entityClass ) {
        if ( !metadataCache.containsKey( entityClass ) && null == metadataCache.putIfAbsent( entityClass, makePromise( entityClass ) ) ) {
            metadataCache.get( entityClass ).run();
        }

        try {
            return metadataCache.get( entityClass ).get();

        } catch ( InterruptedException | ExecutionException e ) {
            logger.error( "Catch async task error", e.getCause() );
            throw new RuntimeException( "Failure to getting entity metadata", e );
        }
    }

    private static FutureTask<EntityMetadata> makePromise( final Class<?> entityClass ) {
        return new FutureTask<>( new Callable<EntityMetadata>() {
            @Override
            public EntityMetadata call() {
                return buildEntity( entityClass );
            }
        } );
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
        Map<Class<?>, ReferenceMetadata> referenceMetadataMap = createAttributeAndInject( metadata, entityClass );

        if ( null == metadata.getId() ) {
            throw new MissingPrimaryKeyException( String.format( "Missing a primary key on entity '%s'", entityClass.getName() ) );
        }

        if ( !referenceMetadataMap.isEmpty() ) {
            Deque<Class<?>> entityClasses = new ArrayDeque<>( referenceMetadataMap.keySet() );
            buildEntityAndInjectToReferenceMetadata( entityClasses.pop(), entityClasses, referenceMetadataMap );
        }

        return metadata;
    }

    private static Map<Class<?>, ReferenceMetadata> buildEntityAndInjectToReferenceMetadata(
            Class<?> target, Deque<Class<?>> entityClasses, Map<Class<?>, ReferenceMetadata> referenceMetadataMap ) {

        referenceMetadataMap.get( target ).setEntityMetadata( buildEntity( target ) );
        return entityClasses.isEmpty() ? referenceMetadataMap : buildEntityAndInjectToReferenceMetadata( entityClasses.pop(), entityClasses, referenceMetadataMap );
    }

    private static Map<Class<?>, ReferenceMetadata> createAttributeAndInject( EntityMetadata metadata, Class<?> entityClass ) {
        List<ReferenceMetadata> referenceMetadataList = metadata.setReferenceColumns( new ArrayList<ReferenceMetadata>() ).getReferenceColumns();
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
        Map<Class<?>, ReferenceMetadata> relationEntityClasses = new HashMap<>();

        Class<?> targetEntity;
        Relation relationship;
        ReferenceMetadata referenceMetadata;

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
                referenceMetadata = createAttributeMetadata( new ReferenceMetadata(), column, field, pd )
                        .setLazy( relationship.isLazy() );

                targetEntity = relationship.getTargetEntity();
                if ( null == targetEntity || void.class == targetEntity || Void.class == targetEntity ) {
                    // see attribute targetEntity of '@ManyToMany, @OneToMany, @ManyToOne, @OneToOne'
                    targetEntity = field.getType();
                }

                relationEntityClasses.put( targetEntity, referenceMetadata );
                referenceMetadataList.add( referenceMetadata );

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
    }

    private static class Relation {
        private final OneToOne oneToOne;
        private final OneToMany oneToMany;
        private final ManyToOne manyToOne;
        private final ManyToMany manyToMany;

        private Relation( Field field, PropertyDescriptor pd ) {
            oneToOne = getJpaAnnotation( OneToOne.class, field, pd );
            oneToMany = getJpaAnnotation( OneToMany.class, field, pd );
            manyToOne = getJpaAnnotation( ManyToOne.class, field, pd );
            manyToMany = getJpaAnnotation( ManyToMany.class, field, pd );

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

            if ( manyToMany != null ) {
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

            if ( manyToMany != null ) {
                return manyToMany.fetch() == FetchType.LAZY;
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

            if ( manyToMany != null ) {
                return manyToMany.targetEntity();
            }

            return null;
        }

        private boolean hasRelationship() {
            return oneToOne != null || oneToMany != null || manyToOne != null || manyToMany != null;
        }
    }
}
