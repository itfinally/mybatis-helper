package top.itfinally.mybatis.jpa.context;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
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
import java.lang.reflect.*;
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

    private static final ConcurrentMap<String, EntityMetadata> mappingsByName = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Class<?>, EntityMetadata> mappingsByClass = new ConcurrentHashMap<>();


    private MetadataFactory() {
    }

    public static void hittingMetadata( List<Class<?>> entityClasses ) {
        Set<ForeignAttributeMetadata> foreignAttributeMetadataTokens = new HashSet<>( 32 );
        for ( Class<?> clazz : entityClasses ) {
            buildEntity( clazz, foreignAttributeMetadataTokens );
        }

        if ( !foreignAttributeMetadataTokens.isEmpty() ) {
            Iterator<ForeignAttributeMetadata> iterator = foreignAttributeMetadataTokens.iterator();
            ForeignAttributeMetadata item;
            String token;

            while ( iterator.hasNext() ) {
                item = iterator.next();
                token = ( ( EntityMetadataToken ) item.getEntityMetadata() ).getToken();

                if ( !( item.getEntityMetadata() instanceof EntityMetadataToken && mappingsByName.containsKey( token ) ) ) {
                    continue;
                }

                item.setEntityMetadata( mappingsByName.get( ( ( EntityMetadataToken ) item.getEntityMetadata() ).getToken() ) );
                injectForeignAttribute( item );

                iterator.remove();
            }
        }

        if ( !foreignAttributeMetadataTokens.isEmpty() ) {
            List<String> names = new ArrayList<>();
            for ( ForeignAttributeMetadata item : foreignAttributeMetadataTokens ) {
                names.add( String.format( "attribute %s of entity %s ( require table %s )",
                        item.getJavaName(), item.getField().getDeclaringClass().getName(),
                        ( ( EntityMetadataToken ) item.getEntityMetadata() ).getToken() ) );
            }

            throw new IllegalStateException( String.format( "Some entities without matches table has been found: ( %s )",
                    Joiner.on( "\n" ).join( names ) ) );
        }
    }

    public static EntityMetadata getMetadata( final Class<?> entityClass ) {
        EntityMetadata metadata = mappingsByClass.get( entityClass );

        if ( null == metadata ) {
            throw new IllegalStateException( "No scan entity before use it, please setting property " +
                    "'mybatis.jpa.entity-scan' in your configurer" );
        }

        return metadata;
    }

    private static void buildEntity( Class<?> entityClass, Set<ForeignAttributeMetadata> foreignAttributeMetadataTokens ) {
        Table table = entityClass.getAnnotation( Table.class );
        if ( null == table ) {
            throw new IllegalStateException( String.format( "Missing annotation '@Table' on entity '%s'", entityClass.getName() ) );
        }

        if ( Strings.isNullOrEmpty( table.name() ) ) {
            throw new IllegalStateException( String.format( "Missing a table name on entity '%s'", entityClass.getName() ) );
        }

        EntityMetadata metadata = !mappingsByClass.containsKey( entityClass )
                ? new EntityMetadata().setEntityClass( entityClass ).setTableName( table.name() )
                : mappingsByClass.get( entityClass );

        createAttributeAndInject( metadata, foreignAttributeMetadataTokens );

        // for table tokens
        mappingsByName.put( metadata.getTableName(), metadata );
        mappingsByClass.put( metadata.getEntityClass(), metadata );

        if ( null == metadata.getId() ) {
            throw new MissingPrimaryKeyException( String.format( "Missing a primary key on entity '%s'", entityClass.getName() ) );
        }
    }

    private static void injectForeignAttribute( ForeignAttributeMetadata foreignAttributeMetadata ) {
        String referenceColumnName = foreignAttributeMetadata.getReferenceAttributeMetadata().getJdbcName();

        foreignAttributeMetadata.setReferenceAttributeMetadata( null );

        List<AttributeMetadata> columns = new ArrayList<>();
        columns.addAll( foreignAttributeMetadata.getEntityMetadata().getColumns() );
        columns.addAll( foreignAttributeMetadata.getEntityMetadata().getReferenceColumns() );

        for ( AttributeMetadata item : columns ) {
            if ( item.getJdbcName().equals( referenceColumnName ) ) {
                foreignAttributeMetadata.setReferenceAttributeMetadata( item );
                break;
            }
        }

        if ( null == foreignAttributeMetadata.getReferenceAttributeMetadata() ) {
            if ( Strings.isNullOrEmpty( referenceColumnName ) ) {
                String fieldName = foreignAttributeMetadata.getField().getName();
                String className = foreignAttributeMetadata.getField().getDeclaringClass().getName();

                throw new IllegalStateException( String.format( "The foreign attribute '%s' of class '%s' ", fieldName, className ) +
                        "have no referenced column name ( require attribute 'referencedColumnName' of @JoinColumn )" );
            }

            throw new IllegalStateException( String.format( "The foreign attribute '%s' is not found at entity '%s'",
                    referenceColumnName, foreignAttributeMetadata.getEntityMetadata().getEntityClass().getName() ) );
        }
    }

    private static void createAttributeAndInject( EntityMetadata metadata, Set<ForeignAttributeMetadata> foreignAttributeMetadataTokens ) {
        List<ForeignAttributeMetadata> foreignAttributeMetadataList = metadata.setReferenceColumns( new ArrayList<ForeignAttributeMetadata>() ).getReferenceColumns();
        List<AttributeMetadata> attributeMetadataList = metadata.setColumns( new ArrayList<AttributeMetadata>() ).getColumns();
        Class<?> entityClass = metadata.getEntityClass();
        BeanInfo beanInfo;

        try {
            beanInfo = beanInfoFactory.getBeanInfo( entityClass );

        } catch ( IntrospectionException e ) {
            throw new RuntimeException( String.format( "Failure to getting getter/setter of entity '%s'", entityClass.getName() ), e );
        }

        if ( null == beanInfo ) {
            throw new IllegalStateException( String.format( "Maybe missing getter/setter method in entity class '%s' ?", entityClass.getName() ) );
        }

        Field field;
        ColumnAnnotation column;
        AttributeMetadata attributeMetadata;

        Class<?> actualType;
        Class<?> attributeType;
        Class<?> targetType = null;

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

            if ( !relationship.hasRelationship() ) {
                attributeMetadata = createAttributeMetadata( new AttributeMetadata(), column, field, pd );
                attributeMetadataList.add( attributeMetadata );

                if ( attributeMetadata.isPrimary() ) {
                    if ( metadata.getId() != null ) {
                        throw new DuplicatePrimaryKeyException( String.format(
                                "Duplicate primary key on entity '%s'", entityClass.getName() ) );
                    }

                    metadata.setId( attributeMetadata );
                }

                continue;
            }

            if ( !column.isJoinColumn() ) {
                throw new IllegalStateException( "Should be use '@JoinColumn' when attribute is marked " +
                        "with relationship annotation like '@OneToOne'" );
            }

            foreignAttributeMetadata = createAttributeMetadata( new ForeignAttributeMetadata(), column, field, pd )
                    .setLazy( relationship.isLazy() );

            attributeType = field.getType();

            // Here to analysis relationship between entities
            // single table query and one to many relationship query( including one to one relationship ).
            //
            // It hard to analysis many to many relationship and mapping it by code, because it is logic in human
            // especially, seem there are no way to generate the right sql to do many to many relationship query
            // This is the reason of why mybatis-jpa unsupported MANY TO MANY relationship query
            //
            // But, it can resolve as one to many relationship
            if ( Collection.class.isAssignableFrom( attributeType ) ) {

                actualType = getActualType( field );

                if ( !( Map.class.isAssignableFrom( actualType ) || actualType.getAnnotation( Table.class ) != null ) ) {
                    throw new IllegalArgumentException( String.format( "The generic type of %s must be an entity " +
                            "what marked with '@Table' or type Map", attributeType.getSimpleName() ) );
                }

                foreignAttributeMetadata.setCollection( true ).setActualType( actualType );

                targetType = Map.class.isAssignableFrom( actualType ) ? null : actualType;

            } else if ( Map.class.isAssignableFrom( attributeType ) ) {
                foreignAttributeMetadata.setCollection( false ).setActualType( attributeType );

            } else if ( attributeType.getAnnotation( Table.class ) != null ) {
                foreignAttributeMetadata.setCollection( false ).setActualType( attributeType );
                targetType = attributeType;

            } else {
                throw new IllegalStateException( String.format( "The foreign attribute have an unknown java type '%s', ", attributeType.getName() ) +
                        "only accept type of 'Collection', 'Map' or any entity marked with '@Table' annotation" );
            }

            if ( targetType != null ) {
                Table table = targetType.getAnnotation( Table.class );
                if ( null == table ) {
                    throw new IllegalStateException( String.format( "The entity '%s' should be marked with '@Table'", targetType.getName() ) );
                }

                foreignAttributeMetadataTokens.add( foreignAttributeMetadata
                        .setReferenceAttributeMetadata( new AttributeMetadata()
                        .setJdbcName( column.joinColumn.referencedColumnName() ) )

                        .setEntityMetadata( new EntityMetadataToken().setToken( table.name() ) ) );

                // clear up
                targetType = null;

            } else if ( !Strings.isNullOrEmpty( column.joinColumn.table() ) ) {
                foreignAttributeMetadataTokens.add( foreignAttributeMetadata
                        .setReferenceAttributeMetadata( new AttributeMetadata()
                        .setJdbcName( column.joinColumn.referencedColumnName() ) )

                        .setEntityMetadata( new EntityMetadataToken().setToken( column.joinColumn.table() ) ) );

            } else {
                throw new IllegalStateException( String.format( "The foreign attribute '%s' of entity '%s' " +
                                "require an entity as attribute type or declare attribute 'table' of '@JoinColumn' to identification",
                        field.getName(), field.getDeclaringClass().getName() ) );
            }

            foreignAttributeMetadataList.add( foreignAttributeMetadata );
        }
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
            throw new NoSuchActualTypeException( "No actual type found from type variables of Collection " +
                    String.format( "at attribute '%s' of class '%s'", field.getName(), field.getDeclaringClass().getName() ) +
                    String.format( "( maybe you missing generic type, expect '%s<ActualType>' but got '%s' )",
                            field.getType().getSimpleName(), field.getType().getSimpleName() ) );
        }

        Type[] actualType = ( ( ParameterizedType ) type ).getActualTypeArguments();
        if ( actualType[ 0 ] instanceof TypeVariable ) {
            throw new NoSuchActualTypeException( "Expect given actual type as type variables but get generic type " +
                    String.format( "at attribute '%s' of class '%s'", field.getName(), field.getDeclaringClass().getName() ) );
        }

        return ( Class<?> ) actualType[ 0 ];
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

        private boolean hasRelationship() {
            return oneToOne != null || oneToMany != null || manyToOne != null;
        }
    }
}
