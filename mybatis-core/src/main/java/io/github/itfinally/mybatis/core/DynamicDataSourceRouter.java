package io.github.itfinally.mybatis.core;

import io.github.itfinally.utils.ThreadLocalDelegatedFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.BeanFactoryDataSourceLookup;

public class DynamicDataSourceRouter extends AbstractRoutingDataSource {
  private static ThreadLocal<String> dataSourceNaming = ThreadLocalDelegatedFactory.newThreadLocal();

  public DynamicDataSourceRouter( BeanFactory beanFactory ) {
    setDataSourceLookup( new BeanFactoryDataSourceLookup( beanFactory ) );
  }

  public static void setDataSourceName( String name ) {
    dataSourceNaming.set( name );
  }

  public static void removeDataSourceName() {
    dataSourceNaming.remove();
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return dataSourceNaming.get();
  }
}
