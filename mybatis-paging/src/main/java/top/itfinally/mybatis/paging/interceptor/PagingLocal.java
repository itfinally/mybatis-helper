package top.itfinally.mybatis.paging.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import top.itfinally.mybatis.paging.PagingItem;
import top.itfinally.mybatis.paging.configuration.MybatisPagingProperties;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/16       itfinally       首次创建
 * *********************************************
 * </pre>
 */
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
