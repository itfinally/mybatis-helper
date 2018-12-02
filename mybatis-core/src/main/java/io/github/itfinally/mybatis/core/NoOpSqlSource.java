package io.github.itfinally.mybatis.core;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

// No-operation sql source
public class NoOpSqlSource implements SqlSource {
  private BoundSql boundSql;

  public NoOpSqlSource( BoundSql boundSql ) {
    this.boundSql = boundSql;
  }

  @Override
  public BoundSql getBoundSql( Object parameterObject ) {
    return boundSql;
  }
}
