package top.itfinally.mybatis.paging;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import top.itfinally.mybatis.paging.collection.PagingList;
import top.itfinally.mybatis.paging.collection.PagingMap;
import top.itfinally.mybatis.paging.collection.PagingSet;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/12       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Intercepts( @Signature( type = Executor.class, method = "query",
        args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class } ) )
public class PagingInterceptor implements Interceptor {

    @Override
    @SuppressWarnings( "unchecked" )
    public Object intercept( Invocation invocation ) throws Throwable {
        Object[] thisArgs = invocation.getArgs();
        MappedStatement mappedStatement = ( MappedStatement ) thisArgs[ 0 ];
        Object[] args = thisArgs.length > 1 ? ( Object[] ) invocation.getArgs()[ 1 ] : new Object[ 0 ];

        mappedStatement.getBoundSql( args ).getSql();

        Object result = invocation.proceed();

        if ( result instanceof List ) {
            return new PagingList<>( ( List<Object> ) result, "" );
        }

        if ( result instanceof Set ) {
            return new PagingSet<>( ( Set<Object> ) result, "" );
        }

        if ( result instanceof Map ) {
            return new PagingMap<>( ( Map<Object, Object> ) result, "" );
        }

        return result;
    }

    @Override
    public Object plugin( Object o ) {
        return Plugin.wrap( o, this );
    }

    @Override
    public void setProperties( Properties properties ) {
    }
}
