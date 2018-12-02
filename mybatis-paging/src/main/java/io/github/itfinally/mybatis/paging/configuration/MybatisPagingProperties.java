package io.github.itfinally.mybatis.paging.configuration;

import org.springframework.stereotype.Component;
import io.github.itfinally.mybatis.core.MybatisCoreConfiguration;
import io.github.itfinally.mybatis.paging.interceptor.hook.SqlHookBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class MybatisPagingProperties extends MybatisCoreConfiguration {
  private Map<String, SqlHookBuilder> sqlHookMap = new HashMap<>();
  private int indexStartingWith;

  public MybatisPagingProperties addSqlHook( String databaseId, SqlHookBuilder sqlHookBuilder ) {
    sqlHookMap.put( databaseId.toLowerCase(), sqlHookBuilder );
    return this;
  }

  public Map<String, SqlHookBuilder> getSqlHookMap() {
    return Collections.unmodifiableMap( sqlHookMap );
  }

  public int getIndexStartingWith() {
    return indexStartingWith;
  }

  public MybatisPagingProperties setIndexStartingWith( int indexStartingWith ) {
    this.indexStartingWith = indexStartingWith;
    return this;
  }
}
