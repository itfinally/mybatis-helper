package io.github.itfinally.mybatis.jpa.sql;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import io.github.itfinally.mybatis.jpa.context.MetadataFactory;
import io.github.itfinally.mybatis.jpa.entity.AttributeMetadata;
import io.github.itfinally.mybatis.jpa.entity.EntityMetadata;
import io.github.itfinally.mybatis.jpa.entity.ForeignAttributeMetadata;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.github.itfinally.mybatis.jpa.mapper.BasicCrudMapper.ENTITY;

// Use it with class BasicCrudMapper
public abstract class BasicCrudSqlCreator {
  private final XMLLanguageDriver languageDriver;

  protected final Configuration configuration;

  protected BasicCrudSqlCreator( Configuration configuration, XMLLanguageDriver languageDriver ) {
    this.configuration = configuration;
    this.languageDriver = languageDriver;
  }

  protected BoundSql buildBoundSql( Object unknownArgs, Function<XMLLanguageDriver, SqlSource> sqlBuilder ) {
    // Add Mybatis sql template in here, which is sql wrapped by #{}.
    // The origin sql will be changed by foreach tag, reference class ForEachSqlNode.
    //
    // For dynamic sql template, Mybatis will translate sql as node and build class DynamicSqlSource with it,
    // and DynamicSqlSource can generate different sql under different parameters.
    return sqlBuilder.apply( languageDriver ).getBoundSql( unknownArgs );
  }

  protected Object getSpecifyParameter( String name, Object unknownArgs ) {
    if ( unknownArgs instanceof MapperMethod.ParamMap ) {
      Map map = ( ( MapperMethod.ParamMap ) unknownArgs );
      return map.containsKey( name ) ? map.get( name ) : Object.class;
    }

    return unknownArgs;
  }

  protected Class<?> getSpecifyParameterType( String name, Object unknownArgs ) {
    Object result = getSpecifyParameter( name, unknownArgs );
    return result != null ? result.getClass() : Object.class;
  }

  protected InsertPair createInsertFieldAndValues( EntityMetadata metadata, Object applier, boolean isNonnull ) {
    List<String> fields = new ArrayList<>();
    List<String> values = new ArrayList<>();

    for ( AttributeMetadata attr : metadata.getColumns() ) {
      if ( isNonnull && isNullValue( applier, attr ) ) {
        continue;
      }

      fields.add( attr.getJdbcName() );
      values.add( String.format( "#{%s.%s}", ENTITY, attr.getJavaName() ) );
    }

    for ( ForeignAttributeMetadata attr : metadata.getReferenceColumns() ) {
      if ( attr.getField().getAnnotation( OneToOne.class ) == null
          || attr.getField().getAnnotation( ManyToOne.class ) == null
          || Map.class.isAssignableFrom( attr.getActualType() ) ) {

        continue;
      }

      // Association field
      Object value = getValue( applier, attr );
      if ( isNonnull && null == value ) {
        continue;
      }

      // Short circuit if value is null
      if ( null == value ) {
        fields.add( attr.getJdbcName() );
        values.add( "null" );

        continue;
      }

      AttributeMetadata id = getIdByEntityClass( attr.getActualType() );
      if ( isNonnull && isNullValue( value, id ) ) {
        continue;
      }

      fields.add( attr.getJdbcName() );
      values.add( String.format( "#{%s.%s.%s}", ENTITY, attr.getJavaName(), id.getJavaName() ) );
    }

    if ( fields.isEmpty() ) {
      throw new IllegalStateException( String.format( "There are no attributes to insert for entity '%s'",
          applier.getClass().getName() ) );
    }

    return new InsertPair( Joiner.on( ", " ).join( fields ), Joiner.on( ", " ).join( values ) );
  }

  protected static class InsertPair {
    private final String fields;
    private final String values;

    public InsertPair( String fields, String values ) {
      this.fields = fields;
      this.values = values;
    }

    public String getFields() {
      return fields;
    }

    public String getValues() {
      return values;
    }
  }

  protected String createUpdateFieldAndValues( EntityMetadata metadata, Object applier, boolean isNonnull ) {
    List<String> pairs = new ArrayList<>();

    for ( AttributeMetadata attr : metadata.getColumns() ) {
      if ( attr.isPrimary() || ( isNonnull && isNullValue( applier, attr ) ) ) {
        continue;
      }

      pairs.add( String.format( "%s = #{%s.%s}", attr.getJdbcName(), ENTITY, attr.getJavaName() ) );
    }

    for ( ForeignAttributeMetadata attr : metadata.getReferenceColumns() ) {
      if ( attr.getField().getAnnotation( OneToOne.class ) == null
          || attr.getField().getAnnotation( ManyToOne.class ) == null
          || Map.class.isAssignableFrom( attr.getActualType() ) ) {

        continue;
      }

      // Association field
      Object value = getValue( applier, attr );
      if ( isNonnull && null == value ) {
        continue;
      }

      // Short circuit if value is null
      if ( null == value ) {
        pairs.add( String.format( "%s = null", attr.getJdbcName() ) );
        continue;
      }

      AttributeMetadata id = getIdByEntityClass( attr.getActualType() );
      if ( isNonnull && isNullValue( value, id ) ) {
        continue;
      }

      pairs.add( String.format( "%s = #{%s.%s.%s}", attr.getJdbcName(), ENTITY, attr.getJavaName(), id.getJavaName() ) );
    }

    if ( pairs.isEmpty() ) {
      throw new IllegalStateException( String.format( "There are no attributes to update for Entity '%s'",
          applier.getClass().getName() ) );
    }

    return Joiner.on( ", " ).join( pairs );
  }

  protected void assertInExpressionNotEmpty( @SuppressWarnings( "all" ) String argName, String methodName, Object unknownArgs ) {
    Object result = getSpecifyParameter( argName, unknownArgs );
    if ( ( result instanceof Collection && ( ( Collection ) result ).isEmpty() ) ) {
      throw new IllegalArgumentException( String.format( "The given collection is empty, method: %s parameter: %s",
          methodName, argName ) );
    }
  }

  private boolean isNullValue( Object applier, AttributeMetadata attr ) {
    return null == getValue( applier, attr );
  }

  private Object getValue( Object applier, AttributeMetadata attr ) {
    try {
      return applier instanceof Map
          ? ( ( Map ) applier ).get( attr.getJavaName() )
          : attr.getReadMethod().invoke( applier );

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      throw new RuntimeException( String.format( "Can't read attribute '%s' from field of entity '%s'",
          attr.getJavaName(), attr.getField().getDeclaringClass() ), e );
    }
  }

  private AttributeMetadata getIdByEntityClass( Class<?> entityClass ) {
    AttributeMetadata id = MetadataFactory.getMetadata( entityClass ).getId();
    if ( null == id ) {
      throw new IllegalStateException( String.format( "There are no id attribute in entity '%s'",
          entityClass.getName() ) );
    }

    return id;
  }

  public abstract BoundSql queryByIdIs( EntityMetadata metadata, Object unknownArgs );

  public abstract BoundSql queryByIdIn( EntityMetadata metadata, Object unknownArgs );

  public abstract BoundSql queryAll( EntityMetadata metadata );

  public abstract BoundSql existByIdIs( EntityMetadata metadata, Object unknownArgs );

  // Try this sql if target database not supported multi values insert
  // insert into table( fields... ) select #{values}... union all select #{values}... union all ...
  public abstract BoundSql save( EntityMetadata metadata, Object unknownArgs );

  public abstract BoundSql saveWithNonnull( EntityMetadata metadata, Object unknownArgs );

  public abstract BoundSql saveAll( EntityMetadata metadata, Object unknownArgs );

  public abstract BoundSql updateByIdIs( EntityMetadata metadata, Object unknownArgs );

  public abstract BoundSql updateWithNonnullByIdIs( EntityMetadata metadata, Object unknownArgs );

  public abstract BoundSql deleteByIdIs( EntityMetadata metadata, Object unknownArgs );

  public abstract BoundSql deleteAllByIdIn( EntityMetadata metadata, Object unknownArgs );
}
