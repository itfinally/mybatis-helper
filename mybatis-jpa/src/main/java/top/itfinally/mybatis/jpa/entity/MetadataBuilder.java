package top.itfinally.mybatis.jpa.entity;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ExtendedBeanInfoFactory;
import top.itfinally.mybatis.jpa.exception.DuplicatePrimaryKeyException;
import top.itfinally.mybatis.jpa.exception.MissingPrimaryKeyException;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
public class MetadataBuilder {
    private static final ConcurrentMap<Class<?>, FutureTask<EntityMetadata>> metadataCache = new ConcurrentHashMap<>();
    private static final ExtendedBeanInfoFactory beanInfoFactory = new ExtendedBeanInfoFactory();
    private static final Logger logger = LoggerFactory.getLogger( MetadataBuilder.class );

    private MetadataBuilder() {
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

        List<AttributeMetadata> attributes = buildAttribute( entityClass );
        AttributeMetadata primaryKey = null;

        for ( AttributeMetadata attr : attributes ) {
            if ( attr.isPrimary() ) {
                if ( primaryKey != null ) {
                    throw new DuplicatePrimaryKeyException( String.format(
                            "Duplicate primary key on entity '%s'", entityClass.getName() ) );
                }

                primaryKey = attr;
            }
        }

        if ( null == primaryKey ) {
            throw new MissingPrimaryKeyException( String.format( "Missing a primary key on entity '%s'", entityClass.getName() ) );
        }

        return new EntityMetadata().setColumns( attributes ).setTableName( table.name() ).setId( primaryKey );
    }

    private static List<AttributeMetadata> buildAttribute( Class<?> entityClass ) {
        List<AttributeMetadata> metadata = new ArrayList<>();
        BeanInfo beanInfo;

        try {
            beanInfo = beanInfoFactory.getBeanInfo( entityClass );

        } catch ( IntrospectionException e ) {
            throw new RuntimeException( String.format( "Failure to getting getter/setter of entity '%s'", entityClass.getName() ), e );
        }

        Field field;
        Column column;

        for ( PropertyDescriptor pd : beanInfo.getPropertyDescriptors() ) {
            if ( "class".equals( pd.getDisplayName() ) ) {
                continue;
            }

            field = getDeclareField( entityClass, pd.getName(), boolean.class == pd.getPropertyType() );
            if ( null == field ) {
                throw new IllegalStateException( String.format( "No such field '%s' on entity '%s'", pd.getName(), entityClass.getName() ) );
            }

            column = getJpaAnnotation( Column.class, field, pd );

            if ( null == column ) {
                throw new IllegalStateException( String.format( "Missing annotation '@Column' on entity field '%s'", field.getName() ) );
            }

            metadata.add( new AttributeMetadata()
                    .setField( field )
                    .setJdbcName( column.name() )
                    .setJavaName( field.getName() )
                    .setNullable( column.nullable() )
                    .setReadMethod( pd.getReadMethod() )
                    .setWriteMethod( pd.getWriteMethod() )
                    .setPrimary( getJpaAnnotation( Id.class, field, pd ) != null ) );
        }

        return metadata;
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
        Method method = pd.getReadMethod();
        T column;

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
}
