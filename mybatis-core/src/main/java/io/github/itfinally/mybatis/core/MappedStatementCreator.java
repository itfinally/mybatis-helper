package io.github.itfinally.mybatis.core;

import com.google.common.base.Joiner;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.session.Configuration;

public class MappedStatementCreator {
  private MappedStatementCreator() {
  }

  public static MappedStatement.Builder getMappedStatementBuilder( MappedStatement mappedStatement, BoundSql boundSql ) {
    MappedStatement.Builder builder = new MappedStatement.Builder( mappedStatement.getConfiguration(),
        mappedStatement.getId(), new NoOpSqlSource( boundSql ), mappedStatement.getSqlCommandType() );

    if ( mappedStatement.getKeyColumns() != null && mappedStatement.getKeyColumns().length > 0 ) {
      builder.keyColumn( Joiner.on( "," ).join( mappedStatement.getKeyColumns() ) );
    }

    if ( mappedStatement.getKeyProperties() != null && mappedStatement.getKeyProperties().length > 0 ) {
      builder.keyProperty( Joiner.on( "," ).join( mappedStatement.getKeyProperties() ) );
    }

    if ( mappedStatement.getResultSets() != null && mappedStatement.getResultSets().length > 0 ) {
      builder.resultSets( Joiner.on( "," ).join( mappedStatement.getResultSets() ) );
    }

    return builder.cache( mappedStatement.getCache() )
        .fetchSize( mappedStatement.getFetchSize() )
        .databaseId( mappedStatement.getDatabaseId() )
        .keyGenerator( mappedStatement.getKeyGenerator() )
        .flushCacheRequired( mappedStatement.isFlushCacheRequired() )

        .lang( mappedStatement.getLang() )
        .timeout( mappedStatement.getTimeout() )
        .useCache( mappedStatement.isUseCache() )
        .resource( mappedStatement.getResource() )
        .resultMaps( mappedStatement.getResultMaps() )

        .parameterMap( mappedStatement.getParameterMap() )
        .resultOrdered( mappedStatement.isResultOrdered() )
        .resultSetType( mappedStatement.getResultSetType() )
        .statementType( mappedStatement.getStatementType() );
  }

  public static MappedStatement copyMappedStatement( MappedStatement mappedStatement, BoundSql boundSql ) {
    return getMappedStatementBuilder( mappedStatement, boundSql ).build();
  }

  public static BoundSql newBoundSql( Configuration configuration, String sql, BoundSql oldBoundSql ) {
    BoundSql newBoundSql = new BoundSql( configuration, sql, oldBoundSql.getParameterMappings(), oldBoundSql.getParameterObject() );

    for ( ParameterMapping item : oldBoundSql.getParameterMappings() ) {
      if ( oldBoundSql.hasAdditionalParameter( item.getProperty() ) ) {
        newBoundSql.setAdditionalParameter( item.getProperty(), oldBoundSql.getAdditionalParameter( item.getProperty() ) );
      }
    }

    if ( oldBoundSql.hasAdditionalParameter( DynamicContext.DATABASE_ID_KEY ) ) {
      newBoundSql.setAdditionalParameter( DynamicContext.DATABASE_ID_KEY, oldBoundSql.getAdditionalParameter( DynamicContext.DATABASE_ID_KEY ) );
    }

    if ( oldBoundSql.hasAdditionalParameter( DynamicContext.PARAMETER_OBJECT_KEY ) ) {
      newBoundSql.setAdditionalParameter( DynamicContext.PARAMETER_OBJECT_KEY, oldBoundSql.getAdditionalParameter( DynamicContext.PARAMETER_OBJECT_KEY ) );
    }

    return newBoundSql;
  }
}
