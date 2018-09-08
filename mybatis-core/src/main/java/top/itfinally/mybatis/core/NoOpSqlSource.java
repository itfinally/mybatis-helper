package top.itfinally.mybatis.core;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/7       itfinally       首次创建
 * *********************************************
 *
 * no-operation sql source
 *
 * </pre>
 */
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
