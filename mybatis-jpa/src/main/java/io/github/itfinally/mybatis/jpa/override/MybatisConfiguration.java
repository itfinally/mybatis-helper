package io.github.itfinally.mybatis.jpa.override;

import com.google.common.collect.Sets;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

// Override the calling chain
// MybatisProperties - Configuration - MapperRegistry - MapperProxyFactory - MapperProxy
@Primary
@org.springframework.context.annotation.Configuration
@ConfigurationProperties( prefix = "mybatis.configuration" )
public class MybatisConfiguration extends Configuration {
  public MybatisConfiguration() {
    try {
      // Use reflect to modify attributes of configuration who using not thread-safe collection as type.
      // So that make mybatis-jpa plugin is thread-safe and efficient.
      // note: THIS CODE IS ONLY EXECUTE ONCE.

      Class<?> clazz = Configuration.class;

      setValue( clazz.getDeclaredField( "mapperRegistry" ), this,
          new MybatisMapperRegistry( this ) );

      setValue( clazz.getDeclaredField( "mappedStatements" ), this,
          new ConcurrentStrictMap<MappedStatement>( "Mapped Statements collection" ) );

      setValue( clazz.getDeclaredField( "caches" ), this,
          new ConcurrentStrictMap<Cache>( "Caches collection" ) );

      setValue( clazz.getDeclaredField( "resultMaps" ), this,
          new ConcurrentStrictMap<ResultMap>( "Result Maps collection" ) );

      setValue( clazz.getDeclaredField( "parameterMaps" ), this,
          new ConcurrentStrictMap<ParameterMap>( "Parameter Maps collection" ) );

      setValue( clazz.getDeclaredField( "keyGenerators" ), this,
          new ConcurrentStrictMap<KeyGenerator>( "Key Generators collection" ) );

      setValue( clazz.getDeclaredField( "loadedResources" ), this,
          Sets.newConcurrentHashSet() );

      setValue( clazz.getDeclaredField( "sqlFragments" ), this,
          new ConcurrentStrictMap<XNode>( "XML fragments parsed from previous mappers" ) );

      setValue( clazz.getDeclaredField( "incompleteStatements" ), this, new LinkedBlockingDeque<>() );
      setValue( clazz.getDeclaredField( "incompleteCacheRefs" ), this, new LinkedBlockingDeque<>() );
      setValue( clazz.getDeclaredField( "incompleteResultMaps" ), this, new LinkedBlockingDeque<>() );
      setValue( clazz.getDeclaredField( "incompleteMethods" ), this, new LinkedBlockingDeque<>() );

    } catch ( NoSuchFieldException | IllegalAccessException e ) {
      throw new RuntimeException( e );
    }
  }

  private static void setValue( Field field, Object applier, Object value ) throws IllegalAccessException {
    field.setAccessible( true );
    field.set( applier, value );
    field.setAccessible( false );
  }

  protected static class ConcurrentStrictMap<V> extends ConcurrentHashMap<String, V> {
    private final String name;

    public ConcurrentStrictMap( String name, int initialCapacity, float loadFactor, int concurrencyLevel ) {
      super( initialCapacity, loadFactor, concurrencyLevel );
      this.name = name;
    }

    public ConcurrentStrictMap( String name, int initialCapacity, float loadFactor ) {
      super( initialCapacity, loadFactor );
      this.name = name;
    }

    public ConcurrentStrictMap( String name, int initialCapacity ) {
      super( initialCapacity );
      this.name = name;
    }

    public ConcurrentStrictMap( String name ) {
      this.name = name;
    }

    public ConcurrentStrictMap( String name, Map<? extends String, ? extends V> m ) {
      super( m );
      this.name = name;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public V put( @Nonnull String key, @Nonnull V value ) {
      if ( get( key, true ) != null ) {
        throw new IllegalArgumentException( name + " already contains value for " + key );
      }

      if ( key.contains( "." ) ) {
        final String shortKey = getShortName( key );
        if ( super.get( shortKey ) == null ) {
          super.put( shortKey, value );
        } else {
          super.put( shortKey, ( V ) new Ambiguity( shortKey ) );
        }
      }

      return super.put( key, value );
    }

    private V get( Object key, boolean containsCheck ) {
      V value = super.get( key );

      // Fix Java7 / Java8 issue
      //
      // On Java7, the 'containsKey' method has independent getting value process.
      // But it merge to the 'get' method on Java8,
      // and 'containsKey' just have one line code( 'get(key) != null;' )
      // to processing value contains judge, so when the 'get' method has been override
      // and use custom value checking, it always raise an exception 'IllegalArgumentException'.
      if ( containsCheck ) {
        return value;
      }

      if ( value == null ) {
        throw new IllegalArgumentException( name + " does not contain value for " + key );
      }

      if ( value instanceof Ambiguity ) {
        throw new IllegalArgumentException( ( ( Ambiguity ) value ).getSubject() + " is ambiguous in " + name
            + " (try using the full name including the namespace, or rename one of the entries)" );
      }

      return value;
    }

    @Override
    public V get( Object key ) {
      return get( key, false );
    }

    private String getShortName( String key ) {
      final String[] keyParts = key.split( "\\." );
      return keyParts[ keyParts.length - 1 ];
    }

    private static class Ambiguity {
      final private String subject;

      private Ambiguity( String subject ) {
        this.subject = subject;
      }

      private String getSubject() {
        return subject;
      }
    }
  }
}