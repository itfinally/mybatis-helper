package top.itfinally.mybatis.core;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.BeanFactoryDataSourceLookup;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/1       itfinally       首次创建
 * *********************************************
 * </pre>
 */
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
