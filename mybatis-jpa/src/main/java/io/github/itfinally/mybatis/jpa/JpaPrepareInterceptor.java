package io.github.itfinally.mybatis.jpa;

import com.google.common.collect.Lists;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import io.github.itfinally.mybatis.core.MappedStatementCreator;
import io.github.itfinally.mybatis.jpa.context.ResultMapFactory;
import io.github.itfinally.mybatis.jpa.mapper.BasicCriteriaQueryInterface;
import io.github.itfinally.mybatis.jpa.sql.BasicCrudSqlCreator;
import io.github.itfinally.mybatis.jpa.context.CrudContextHolder;
import io.github.itfinally.mybatis.jpa.sql.JpaSqlCreator;
import io.github.itfinally.mybatis.jpa.sql.MysqlCrudSqlCreator;
import io.github.itfinally.mybatis.jpa.sql.SqliteCrudSqlCreator;
import io.github.itfinally.mybatis.jpa.utils.TypeMatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static io.github.itfinally.mybatis.core.MybatisCoreConfiguration.MYSQL;
import static io.github.itfinally.mybatis.core.MybatisCoreConfiguration.SQLITE;

@Component
@Intercepts( {
    @Signature( type = Executor.class, method = "update", args = { MappedStatement.class, Object.class } ),
    @Signature( type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class } ),
    @Signature( type = Executor.class, method = "queryCursor", args = { MappedStatement.class, Object.class, RowBounds.class } ),
    @Signature( type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class } )
} )
public class JpaPrepareInterceptor implements Interceptor {
  private final BasicCrudSqlCreator sqlCreator;
  private final JpaSqlCreator jpaSqlCreator;

  private final Map<String, Class<?>[]> parameters;
  private final Map<String, Method> methods;

  public JpaPrepareInterceptor( MybatisJpaConfiguration jpaConfig, Configuration configuration ) {
    XMLLanguageDriver languageDriver = new XMLLanguageDriver();

    switch ( jpaConfig.getDatabaseId() ) {
      case MYSQL: {
        sqlCreator = new MysqlCrudSqlCreator( configuration, languageDriver );
        break;
      }

      case SQLITE: {
        sqlCreator = new SqliteCrudSqlCreator( configuration, languageDriver );
        break;
      }

      default: {
        throw new UnsupportedOperationException( String.format( "No match database '%s'", jpaConfig.getDatabaseId() ) );
      }
    }

    jpaSqlCreator = new JpaSqlCreator( configuration, languageDriver );

    Map<String, Method> methods = new HashMap<>();
    Map<String, Class<?>[]> parameters = new HashMap<>();
    for ( Method method : sqlCreator.getClass().getDeclaredMethods() ) {
      methods.put( method.getName(), method );
      parameters.put( method.getName(), method.getParameterTypes() );
    }

    this.methods = Collections.unmodifiableMap( methods );
    this.parameters = Collections.unmodifiableMap( parameters );
  }

  @Override
  public Object intercept( Invocation invocation ) throws Throwable {
    CrudContextHolder.Context context = CrudContextHolder.getContext();
    if ( null == context ) {
      return invocation.proceed();
    }

    if ( context.getContextType() == CrudContextHolder.ContextType.GENERAL ) {
      genericQuery( invocation.getArgs(), context );

    } else if ( context.getContextType() == CrudContextHolder.ContextType.JPA ) {
      jpaQuery( invocation.getArgs(), context );

    } else {
      throw new IllegalStateException( "Unknown context type: " + context.getContextType() );
    }

    return invocation.proceed();
  }

  @Override
  public Object plugin( Object target ) {
    return Plugin.wrap( target, this );
  }

  @Override
  public void setProperties( Properties properties ) {
  }

  @SuppressWarnings( "unchecked" )
  private void jpaQuery( Object[] args, CrudContextHolder.Context context ) {
    MappedStatement mappedStatement = ( MappedStatement ) args[ 0 ];
    Map<String, Object> unknownArgs = ( Map<String, Object> ) args[ 1 ];

    MappedStatement.Builder mappedStatementBuilder = MappedStatementCreator
        .getMappedStatementBuilder( mappedStatement, jpaSqlCreator.buildSql( unknownArgs ) );

    if ( mappedStatement.getSqlCommandType() == SqlCommandType.SELECT ) {
      Class<?> entityClass = ( Class<?> ) unknownArgs.get( BasicCriteriaQueryInterface.ENTITY_CLASS );

      ResultMap resultMap =

          TypeMatcher.isBasicType( entityClass )
              ? ResultMapFactory.getResultMapWithBasicTypeReturned( mappedStatement.getConfiguration(), entityClass )

              : Map.class.isAssignableFrom( entityClass )
              ? ResultMapFactory.getResultMapWithMapReturned( mappedStatement.getConfiguration() )

              : ResultMapFactory.getResultMap( mappedStatement.getConfiguration(), context );

      mappedStatementBuilder.resultMaps( Lists.newArrayList( resultMap ) );
    }

    args[ 0 ] = mappedStatementBuilder.build();
  }

  private void genericQuery( Object[] args, CrudContextHolder.Context context ) {
    MappedStatement mappedStatement = ( MappedStatement ) args[ 0 ];
    Object unknownArgs = args[ 1 ];

    MappedStatement.Builder mappedStatementBuilder = MappedStatementCreator
        .getMappedStatementBuilder( mappedStatement, getBoundSql( context, unknownArgs ) );

    if ( mappedStatement.getSqlCommandType() == SqlCommandType.SELECT ) {
      List<ResultMap> originResultMap = mappedStatement.getResultMaps();

      if ( originResultMap.isEmpty() ) {
        throw new IllegalStateException( "The origin result map is empty." );
      }

      if ( !TypeMatcher.isBasicType( originResultMap.get( 0 ).getType() ) ) {
        ResultMap resultMap = ResultMapFactory.getResultMap( mappedStatement.getConfiguration(), context );
        mappedStatementBuilder.resultMaps( Lists.newArrayList( resultMap ) );
      }
    }

    args[ 0 ] = mappedStatementBuilder.build();
  }

  private BoundSql getBoundSql( CrudContextHolder.Context context, Object unknownArgs ) {
    try {
      if ( !methods.containsKey( context.getMethod().getName() ) ) {
        throw new RuntimeException( new NoSuchMethodException( String.format(
            "Failure to getting method '%s' from sql creator", context.getMethod().getName() ) ) );
      }

      Method method = methods.get( context.getMethod().getName() );
      switch ( parameters.get( method.getName() ).length ) {
        case 1: {
          return ( BoundSql ) method.invoke( sqlCreator, context.getMetadata() );
        }

        case 2: {
          return ( BoundSql ) method.invoke( sqlCreator, context.getMetadata(), unknownArgs );
        }

        default: {
          throw new UnsupportedOperationException( String.format( "Method: %s parameter length: %d not found",
              method.getName(), parameters.get( method.getName() ).length ) );
        }
      }

    } catch ( IllegalAccessException | InvocationTargetException e ) {
      throw new RuntimeException( String.format( "Failure to invoke method '%s' by sqlSource object",
          context.getMethod().getName() ), e );
    }
  }
}
