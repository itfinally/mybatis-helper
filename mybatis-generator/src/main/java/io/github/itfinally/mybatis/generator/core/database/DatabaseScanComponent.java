package io.github.itfinally.mybatis.generator.core.database;

import com.google.common.base.Strings;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import io.github.itfinally.mybatis.generator.configuration.MybatisGeneratorProperties;
import io.github.itfinally.mybatis.generator.core.TypeMappings;
import io.github.itfinally.mybatis.generator.configuration.MybatisGeneratorConfiguration;
import io.github.itfinally.mybatis.generator.configuration.NamingMapping;
import io.github.itfinally.mybatis.generator.core.NamingConverter;
import io.github.itfinally.mybatis.generator.core.PrimitiveType;
import io.github.itfinally.mybatis.generator.core.database.entity.ColumnEntity;
import io.github.itfinally.mybatis.generator.core.database.entity.TableEntity;
import io.github.itfinally.mybatis.generator.core.database.mapper.MetadataMapper;
import io.github.itfinally.mybatis.generator.core.database.mapper.MysqlMetadataMapper;
import io.github.itfinally.mybatis.generator.core.database.mapper.OracleMetadataMapper;
import io.github.itfinally.mybatis.generator.core.database.mapper.SqliteMetadataMapper;
import io.github.itfinally.mybatis.generator.exception.UnknownNameMappingException;
import io.github.itfinally.mybatis.generator.exception.UnknownTypeException;

import javax.annotation.Resource;
import java.util.*;

import static io.github.itfinally.mybatis.core.MybatisCoreConfiguration.MYSQL;
import static io.github.itfinally.mybatis.core.MybatisCoreConfiguration.ORACLE;
import static io.github.itfinally.mybatis.core.MybatisCoreConfiguration.SQLITE;


public abstract class DatabaseScanComponent {
  private static Logger logger = LoggerFactory.getLogger( DatabaseScanComponent.class );

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

  protected TableEntity buildTableNameInJava( TableEntity table ) {
    String javaName = null;
    if ( namingMapping != null ) {
      javaName = namingMapping.getMapping( table.getJdbcName() );
    }

    if ( null == namingMapping || table.getJdbcName().equals( javaName ) ) {
      logger.warn( String.format( "There are no naming mapping to offer, convert underline-case to camel-case by default. " +
          "target table: %s", table.getJdbcName() ) );

      return table.setJavaName( namingConverter.convert( table.getJdbcName(), false ).replaceAll( "^\\w",
          Character.toString( table.getJdbcName().charAt( 0 ) ).toUpperCase() ) );
    }

    if ( Strings.isNullOrEmpty( javaName ) ) {
      throw new UnknownNameMappingException( String.format( "No mapping found for table '%s'", table.getJdbcName() ) );
    }

    return table.setJavaName( javaName );
  }

  // If want to provide alias for some type, you should be override it.
  protected void jdbcTypeAliasPatches( Map<String, String> typePatches ) {
  }

  // Based on attribute name and java type to create getter / setter method.
  // For some attributes like the name begin with 'is' and java type is primitive integer etc...
  // It will translate type int to boolean
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
          .setSetterName( String.format( "set%s", attrName ) );

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

        case SQLITE: {
          metadataMapper = context.getBean( SqliteMetadataMapper.class );
          activeClass = SqliteScanComponent.class;
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
