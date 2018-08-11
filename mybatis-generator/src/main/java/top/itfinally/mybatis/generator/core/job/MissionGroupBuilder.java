package top.itfinally.mybatis.generator.core.job;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.itfinally.mybatis.generator.configuration.MybatisGeneratorProperties;
import top.itfinally.mybatis.generator.core.database.entity.ColumnEntity;
import top.itfinally.mybatis.generator.core.database.entity.TableEntity;

import javax.annotation.Resource;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/31       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Component
@NotThreadSafe
public class MissionGroupBuilder {

    @Resource
    private MybatisGeneratorProperties properties;

    private static MybatisGeneratorProperties middleProperties;

    public List<JobGroup> build( List<TableEntity> tables ) {
        middleProperties = properties;
        List<JobGroup> groups = new ArrayList<>( tables.size() );

        JobGroup group;
        for ( TableEntity table : tables ) {
            group = new JobGroup()
                    .setTableEntity( table )
                    .setEntity( buildEntity( table ) )
                    .setMapperXml( buildMapperXml( table ) )
                    .setRepository( buildRepository( table ) );

            if ( properties.isIncludeServiceInterfaces() ) {
                group.setServicesInterface( buildServiceInterface( table ) );
            }

            if ( properties.isIncludeController() ) {
                group.setController( buildController( table ) );
            }

            if ( properties.isIncludeServices() ) {
                group.setServices( buildService( table ) );
            }

            if ( !StringUtils.isEmpty( properties.getSuperEntity() ) ) {
                extractSuperEntityProperty( table, group.getEntity() );
            }

            groups.add( group );
        }

        middleProperties = null;
        return groups;
    }

    private JobUnit buildEntity( TableEntity table ) {
        String className = String.format( "%sEntity", table.getJavaName() );

        JobUnit job = new JobUnit()
                .setBlackLine( 1 )
                .setClassName( className )
                .setPackageName( properties.getEntityPackage() )
                .setWritePath( buildJavaFilePath( properties.getEntityPackage(), className ) );

        if ( !StringUtils.isEmpty( properties.getSuperEntity() ) ) {
            job.setSuperClass( SuperEntityBuilder.cls );
        }

        return job;
    }

    private JobUnit buildRepository( TableEntity table ) {
        String className = String.format( "%sMapper", table.getJavaName() );

        JobUnit job = new JobUnit()
                .setBlackLine( 1 )
                .setClassName( className )
                .setPackageName( properties.getRepositoryPackage() )
                .setWritePath( buildJavaFilePath( properties.getRepositoryPackage(), className ) );

        if ( !StringUtils.isEmpty( properties.getSuperRepository() ) ) {
            job.setSuperClass( SuperRepositoryBuilder.cls );
        }

        return job;
    }

    private JobUnit buildMapperXml( TableEntity table ) {
        return new JobUnit().setWritePath( buildXmlFilePath( table ) );
    }

    private JobUnit buildService( TableEntity table ) {
        String className = String.format( properties.isIncludeServiceInterfaces() ? "%sServiceImpl" : "%sService", table.getJavaName() );

        JobUnit job = new JobUnit()
                .setBlackLine( 1 )
                .setClassName( className )
                .setPackageName( properties.getServicePackage() )
                .setWritePath( buildJavaFilePath( properties.getServicePackage(), className ) );

        if ( !StringUtils.isEmpty( properties.getSuperServices() ) ) {
            job.setSuperClass( SuperServicesBuilder.cls );
        }

        return job;
    }

    private JobUnit buildServiceInterface( TableEntity table ) {
        String className = String.format( "%sService", table.getJavaName() );

        JobUnit job = new JobUnit()
                .setBlackLine( 1 )
                .setClassName( className )
                .setPackageName( properties.getServicesInterfacePackage() )
                .setWritePath( buildJavaFilePath( properties.getServicesInterfacePackage(), className ) );

        if ( !StringUtils.isEmpty( properties.getSuperServicesInterface() ) ) {
            job.setSuperClass( SuperServicesInterfaceBuilder.cls );
        }

        return job;
    }

    private JobUnit buildController( TableEntity table ) {
        String className = String.format( "%sController", table.getJavaName() );

        JobUnit job = new JobUnit()
                .setBlackLine( 1 )
                .setClassName( className )
                .setPackageName( properties.getControllerPackage() )
                .setWritePath( buildJavaFilePath( properties.getControllerPackage(), className ) );

        if ( !StringUtils.isEmpty( properties.getSuperController() ) ) {
            job.setSuperClass( SuperControllerBuilder.cls );
        }

        return job;
    }

    private static class SuperEntityBuilder {
        static Class<?> cls = loadClass( middleProperties.getSuperEntity() );
    }

    private static class SuperRepositoryBuilder {
        static Class<?> cls = loadClass( middleProperties.getSuperRepository() );
    }

    private static class SuperServicesBuilder {
        static Class<?> cls = loadClass( middleProperties.getSuperServices() );
    }

    private static class SuperServicesInterfaceBuilder {
        static Class<?> cls = loadClass( middleProperties.getServicesInterfacePackage() );
    }

    private static class SuperControllerBuilder {
        static Class<?> cls = loadClass( middleProperties.getSuperController() );
    }

    private String buildJavaFilePath( String packagePath, String className ) {
        return String.format( "%s%s%s%s%s.java", properties.getJavaFilePath(), File.separator, packagePath
                .replaceAll( "\\.", File.separator ), File.separator, className );
    }

    private String buildXmlFilePath( TableEntity table ) {
        return String.format( "%s%s%s.xml", properties.getResourcesPath(), File.separator, table.getJavaName() );
    }

    private static Class<?> loadClass( String className ) {
        try {
            return Class.forName( className );

        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( String.format( "Failure to load class of '%s'", className ), e );
        }
    }

    private void extractSuperEntityProperty( TableEntity table, JobUnit jobUnit ) {
        Map<String, Field> fields = new HashMap<>();
        for ( Field field : jobUnit.getSuperClass().getDeclaredFields() ) {
            fields.put( field.getName(), field );
        }

        Field field;
        for ( ColumnEntity column : table.getColumns() ) {
            field = fields.get( column.getJavaName() );

            if ( null == field ) {
                continue;
            }

            if ( field.getName().equals( column.getJavaName() )
                    && ( field.getType() == column.getJavaTypeClass()
                    || unBoxAndCompare( field.getType(), column.getJavaTypeClass() ) ) ) {

                column.setHidden( true );
            }
        }

        // 重设当前实体的依赖
        table.getDepends().clear();

        Class<?> typeCls;
        for ( ColumnEntity item : table.getColumns() ) {
            typeCls = item.getJavaTypeClass();

            if ( !StringUtils.isEmpty( typeCls.getName().matches(
                    "^java\\.lang.*|^\\[.*" ) ? "" : typeCls.getName() ) ) {

                table.addDepend( typeCls.getName() );
            }
        }
    }

    private boolean unBoxAndCompare( Class<?> target, Class<?> source ) {
        Class<?> localTarget = PrimitiveType.getType( target );
        Class<?> localSource = PrimitiveType.getType( source );

        return localTarget != null && localTarget == localSource;
    }

    private static class PrimitiveType {
        private static final PrimitiveType primitiveType = new PrimitiveType();
        private static List<Method> types;

        static {
            List<Method> types = new ArrayList<>();
            Method[] methods = PrimitiveType.class.getDeclaredMethods();
            for ( Method method : methods ) {
                if ( !"getType".equals( method.getName() ) && method.getName().matches( ".*Type$" ) ) {
                    method.setAccessible( true );
                    types.add( method );
                }
            }

            PrimitiveType.types = Collections.unmodifiableList( types );
        }

        private static Class<?> getType( Class<?> type ) {
            Object result;
            for ( Method method : types ) {
                try {
                    result = method.invoke( primitiveType, type );
                    if ( result != null ) {
                        return ( Class<?> ) result;
                    }

                } catch ( IllegalAccessException | InvocationTargetException e ) {
                    throw new RuntimeException( e );
                }
            }

            return null;
        }

        private Class<?> byteType( Class<?> type ) {
            return byte.class == type ? type : Byte.class == type ? byte.class : null;
        }

        private Class<?> booleanType( Class<?> type ) {
            return boolean.class == type ? type : Boolean.class == type ? boolean.class : null;
        }

        private Class<?> shortType( Class<?> type ) {
            return short.class == type ? type : Short.class == type ? short.class : null;
        }

        private Class<?> charType( Class<?> type ) {
            return char.class == type ? type : Character.class == type ? char.class : null;
        }

        private Class<?> intType( Class<?> type ) {
            return int.class == type ? type : Integer.class == type ? int.class : null;
        }

        private Class<?> longType( Class<?> type ) {
            return long.class == type ? type : Long.class == type ? long.class : null;
        }

        private Class<?> floatType( Class<?> type ) {
            return float.class == type ? type : Float.class == type ? float.class : null;
        }

        private Class<?> doubleType( Class<?> type ) {
            return double.class == type ? type : Double.class == type ? double.class : null;
        }
    }
}
