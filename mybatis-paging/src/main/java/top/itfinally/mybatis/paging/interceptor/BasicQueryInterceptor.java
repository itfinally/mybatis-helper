package top.itfinally.mybatis.paging.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/12       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Order
@Component
@SuppressWarnings( "unchecked" )
@Intercepts( {
        @Signature( type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class } ),
        @Signature( type = Executor.class, method = "queryCursor", args = { MappedStatement.class, Object.class, RowBounds.class } )
} )
public class BasicQueryInterceptor extends AbstractPagingInterceptor {

    public BasicQueryInterceptor( List<DataSource> dataSources ) {
        super( dataSources );
    }

    @Override
    protected void hook( Object[] thisArgs, MappedStatement mappedStatement, BoundSql boundSql ) {
        thisArgs[ 0 ] = mappedStatement;
    }
}
