package top.itfinally.mybatis.paging.configuration;

import top.itfinally.mybatis.paging.interceptor.hook.SqlHookBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/17       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class MybatisPagingProperties {
    public static final String MYSQL = "mysql";
    public static final String ORACLE = "oracle";

    private final Map<String, SqlHookBuilder> sqlHookMap = new HashMap<>();
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
