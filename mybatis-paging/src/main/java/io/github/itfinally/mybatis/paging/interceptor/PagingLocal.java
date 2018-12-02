package io.github.itfinally.mybatis.paging.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import io.github.itfinally.mybatis.paging.PagingItem;
import io.github.itfinally.mybatis.paging.configuration.MybatisPagingProperties;

public class PagingLocal {
  private static ThreadLocal<PagingItem> pagingLocal;
  private static int indexStartingWith;

  protected static void setPagingLocal( @SuppressWarnings( "all" ) ThreadLocal<PagingItem> pagingLocal ) {
    PagingLocal.pagingLocal = pagingLocal;
  }

  static PagingItem getPagingItem() {
    return null == pagingLocal ? null : pagingLocal.get();
  }

  protected static int getIndexStartingWith() {
    return indexStartingWith;
  }

  @Configuration
  static class Configurer {
    public Configurer( @Autowired( required = false ) MybatisPagingProperties properties ) {
      indexStartingWith = null == properties ? 0 : properties.getIndexStartingWith();
    }
  }
}
