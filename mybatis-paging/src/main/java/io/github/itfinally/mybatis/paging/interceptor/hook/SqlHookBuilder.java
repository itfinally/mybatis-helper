package io.github.itfinally.mybatis.paging.interceptor.hook;

import net.sf.jsqlparser.JSQLParserException;

public interface SqlHookBuilder {
  SqlHook build( String originSql, long beginRow, long range ) throws JSQLParserException;
}
