package top.itfinally.mybatis.paging.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import top.itfinally.mybatis.core.MybatisCoreConfiguration;
import top.itfinally.mybatis.paging.interceptor.hook.SqlHookBuilder;

import javax.sql.DataSource;
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
