package top.itfinally.mybatis.paging.interceptor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

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
@SuppressWarnings( "unchecked" )
@Intercepts( @Signature( type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class } ) )
public class CacheQueryInterceptor extends AbstractPagingInterceptor {

    public CacheQueryInterceptor( DataSource dataSource ) {
        super( dataSource );
    }

    @Override
    protected void hook( Object[] thisArgs, MappedStatement mappedStatement, BoundSql boundSql ) {
        thisArgs[ 0 ] = mappedStatement;
        thisArgs[ 5 ] = boundSql;
    }
}