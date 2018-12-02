package io.github.itfinally.mybatis.jpa.context;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import io.github.itfinally.mybatis.jpa.entity.EntityMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

public class ResultMapFactory {
  private static volatile ResultMap resultMapWithTypeMap;
  private static final ConcurrentMap<Class<?>, ResultMap> resultMapWithBasicType = new ConcurrentHashMap<>();

  private static final Cache<String, ResultMap> resultMaps = CacheBuilder.newBuilder()
      .concurrencyLevel( Runtime.getRuntime().availableProcessors() )
      .expireAfterWrite( 30, TimeUnit.MINUTES )
      .initialCapacity( 128 )
      .maximumSize( 51200 )
      .build();

  private ResultMapFactory() {
  }

  public static ResultMap getResultMap( final Configuration configuration, CrudContextHolder.Context context ) {
    final ResultMapToken token = new ResultMapToken( context );
    final EntityMetadata metadata = context.getMetadata();

    try {
      return resultMaps.get( token.getNamespace(), new Callable<ResultMap>() {
        @Override
        public ResultMap call() throws Exception {
          if ( !configuration.hasResultMap( token.getResultMapId() ) ) {

            try ( InputStream in = new ByteArrayInputStream( ResultMapBuilder
                .build( token, metadata ).getBytes() ) ) {

              new XMLMapperBuilder( in, configuration, token.getResultMapId(), configuration.getSqlFragments() ).parse();
            }
          }

          return configuration.getResultMap( token.getResultMapId() );
        }
      } );

    } catch ( ExecutionException e ) {
      throw new RuntimeException( "Failure to load result map", e.getCause() );
    }
  }

  public static ResultMap getResultMapWithMapReturned( Configuration configuration ) {
    if ( null == resultMapWithTypeMap ) {
      resultMapWithTypeMap = new ResultMap.Builder( configuration, "", Map.class, new ArrayList<ResultMapping>() ).build();
    }

    return resultMapWithTypeMap;
  }

  public static ResultMap getResultMapWithBasicTypeReturned( Configuration configuration, Class<?> type ) {
    if ( !resultMapWithBasicType.containsKey( type ) ) {
      resultMapWithBasicType.put( type, new ResultMap.Builder( configuration, "", type, new ArrayList<ResultMapping>() ).build() );
    }

    return resultMapWithBasicType.get( type );
  }

  static class ResultMapToken {
    static final String PREFIX = "dynamic_";

    private static final HashFunction hashFunction = Hashing.hmacMd5( ResultMapToken.class.getName().getBytes() );

    private final String namespace;
    private final String resultMapId;
    private final String hashedCacheKey;

    private ResultMapToken( CrudContextHolder.Context context ) {
      this.namespace = context.getMetadata().getEntityClass().getName();

      this.hashedCacheKey = hashFunction.newHasher().putString( namespace, Charsets.UTF_8 ).hash().toString();

      this.resultMapId = String.format( "%s.%s%s", namespace, PREFIX, hashedCacheKey );
    }

    String getNamespace() {
      return namespace;
    }

    String getResultMapId() {
      return resultMapId;
    }

    String getHashedCacheKey() {
      return hashedCacheKey;
    }
  }
}
