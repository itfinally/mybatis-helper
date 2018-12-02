package io.github.itfinally.mybatis.paging.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import io.github.itfinally.mybatis.paging.configuration.MybatisPagingProperties;

@Component
@SuppressWarnings( "unchecked" )
@Intercepts( {
    @Signature( type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class } ),
    @Signature( type = Executor.class, method = "queryCursor", args = { MappedStatement.class, Object.class, RowBounds.class } )
} )
public class BasicQueryInterceptor extends AbstractPagingInterceptor {

  public BasicQueryInterceptor( MybatisPagingProperties properties ) {
    super( properties );
  }

  @Override
  protected void hook( Object[] thisArgs, MappedStatement mappedStatement, BoundSql boundSql ) {
    thisArgs[ 0 ] = mappedStatement;
  }
}
