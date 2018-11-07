package top.itfinally.mybatis.generator.core.database;

import com.google.common.base.Strings;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import top.itfinally.mybatis.generator.configuration.MybatisGeneratorProperties;
import top.itfinally.mybatis.generator.core.TypeMappings;
import top.itfinally.mybatis.generator.configuration.MybatisGeneratorConfiguration;
import top.itfinally.mybatis.generator.configuration.NamingMapping;
import top.itfinally.mybatis.generator.core.NamingConverter;
import top.itfinally.mybatis.generator.core.PrimitiveType;
import top.itfinally.mybatis.generator.core.database.entity.ColumnEntity;
import top.itfinally.mybatis.generator.core.database.entity.TableEntity;
import top.itfinally.mybatis.generator.core.database.mapper.MetadataMapper;
import top.itfinally.mybatis.generator.core.database.mapper.MysqlMetadataMapper;
import top.itfinally.mybatis.generator.core.database.mapper.OracleMetadataMapper;
import top.itfinally.mybatis.generator.exception.UnknownTypeException;

import javax.annotation.Resource;
import java.util.*;

import static top.itfinally.mybatis.core.MybatisCoreConfiguration.MYSQL;
import static top.itfinally.mybatis.core.MybatisCoreConfiguration.ORACLE;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public abstract class DatabaseScanComponent {
    private static Map<String, Class<?>> jdbcToJavaMappings;
    private static Map<String, String> jdbcTypeAliasMappings;

    protected static MetadataMapper metadataMapper;

    @Resource
    protected NamingConverter namingConverter;

    @Resource
    protected MybatisGeneratorProperties properties;

    @Autowired( required = false )
    protected NamingMapping namingMapping;

    public abstract List<TableEntity> getTables();

    /**
     *  ***************************************************
     *    Version       Date          Author        Desc ( 一句话描述修改 )
     *  ---------------------------------------------------
     *    v1.0     2018/8/9        itfinally        首次创建
     *  ***************************************************
     *
     * 该函数用于提供某些类型的别名,
     * 当数据库获取的类型与 Mybatis 的类型映射匹配不上时会搜索 jdbcTypeAliasMappings.
     *
     * 如果希望为某些类型的映射提供别名, 应当覆盖该方法.
     *
     * 当 jdbcTypeAliasMappings 有合适的类型映射后会覆盖原有的 jdbcType 并提供对应的 javaType,
     * 以上流程由 {@link Builder#getJavaType(ColumnEntity)} 实现
     *
     * @param typePatches 针对 jdbcType 属性的类型映射集合, key 为类型别名, value 为需要映射的类型名,
     *                    并且 value 必须存在是 {@link TypeHandlerRegistry} 的构造器里面映射的类型
     */
    protected void jdbcTypeAliasPatches( Map<String, String> typePatches ) {
    }

    /**
     *  ***************************************************
     *    Version       Date          Author        Desc ( 一句话描述修改 )
     *  ---------------------------------------------------
     *    v1.0     2018/8/9        itfinally        首次创建
     *  ***************************************************
     *
     * 根据属性名和 javaType 创建合适的 getter / setter 方法.
     * 主要是针对 javaType 为整型( 含短整型和长整型 ), 并且 jdbcName 以 is 开头的属性,
     * 该属性将统一转换为 Boolean 类型
     */
    protected void buildGS( ColumnEntity column ) {
        String attrName;
        char[] javaNameChars;

        Class<?> type = PrimitiveType.getType( column.getJavaTypeClass() );
        if ( column.getJdbcName().startsWith( "is" ) && ( byte.class == type
                || short.class == type || int.class == type || long.class == type ) ) {

            javaNameChars = column
                    .setJavaType( "boolean" )
                    .setJavaTypeClass( boolean.class )

                    .getJavaName()
                    .replaceFirst( "^is", "" ).toCharArray();

            attrName = new String( javaNameChars );

            javaNameChars[ 0 ] = Character.toLowerCase( javaNameChars[ 0 ] );
            column.setJavaName( new String( javaNameChars ) )
                    .setGetterName( String.format( "is%s", attrName ) )
                    .setSetterName(  String.format( "set%s", attrName ) );

        } else {
            javaNameChars = column.getJavaName().toCharArray();
            javaNameChars[ 0 ] = Character.toUpperCase( javaNameChars[ 0 ] );

            attrName = new String( javaNameChars );

            column.setGetterName( String.format( "get%s", attrName ) )
                    .setSetterName( String.format( "set%s", attrName ) );
        }
    }

    protected static Class<?> getJavaType( ColumnEntity column ) {
        String jdbcTypeName = column.getJdbcType();
        Class<?> javaTypeClass = jdbcToJavaMappings.get( jdbcTypeName );

        if ( javaTypeClass != null ) {
            return javaTypeClass;
        }

        String jdbcTypeAlias = jdbcTypeAliasMappings.get( jdbcTypeName );
        if ( !Strings.isNullOrEmpty( jdbcTypeAlias ) && jdbcToJavaMappings.containsKey( jdbcTypeAlias ) ) {
            return jdbcToJavaMappings.get( column.setJdbcType( jdbcTypeAlias ).getJdbcType() );
        }

        throw new UnknownTypeException( String.format( "No mapping found for jdbc type '%s'", column.getJdbcType() ) );
    }

    @Component
    public static class Builder {
        private ApplicationContext context;

        private Class<? extends DatabaseScanComponent> activeClass;

        public Builder( MybatisGeneratorConfiguration configuration, ApplicationContext context ) {
            switch ( configuration.getDatabaseId() ) {
                case MYSQL: {
                    metadataMapper = context.getBean( MysqlMetadataMapper.class );
                    activeClass = MysqlScanComponent.class;
                    break;
                }

                case ORACLE: {
                    metadataMapper = context.getBean( OracleMetadataMapper.class );
                    activeClass = OracleScanComponent.class;
                    break;
                }

                default: {
                    throw new UnsupportedOperationException( String.format( "Not match database id( '%s' ).", configuration.getDatabaseId() ) );
                }
            }

            initTypeMapping( context );
            this.context = context;
        }

        private void initTypeMapping( ApplicationContext context ) {
            Map<String, Class<?>> jdbcToJavaMappings = new HashMap<>();
            Map<String, String> jdbcTypeAliasMappings = new HashMap<>();

            Map<JdbcType, Class<?>> mapping = TypeMappings.jdbcMapping;

            for ( JdbcType item : JdbcType.values() ) {
                if ( !mapping.containsKey( item ) ) {
                    continue;
                }

                jdbcToJavaMappings.put( item.toString(), mapping.get( item ) );
            }

            context.getBean( activeClass ).jdbcTypeAliasPatches( jdbcTypeAliasMappings );

            DatabaseScanComponent.jdbcToJavaMappings = Collections.unmodifiableMap( jdbcToJavaMappings );
            DatabaseScanComponent.jdbcTypeAliasMappings = Collections.unmodifiableMap( jdbcTypeAliasMappings );
        }

        public DatabaseScanComponent getScanComponent() {
            return context.getBean( activeClass );
        }
    }
}
