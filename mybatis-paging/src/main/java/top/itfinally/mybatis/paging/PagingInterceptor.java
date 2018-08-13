package top.itfinally.mybatis.paging;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import top.itfinally.mybatis.paging.collection.PagingList;
import top.itfinally.mybatis.paging.collection.PagingMap;
import top.itfinally.mybatis.paging.collection.PagingSet;

import java.util.*;

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
@SuppressWarnings( "unchecked" )
@Intercepts( @Signature( type = Executor.class, method = "query",
        args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class } ) )
public class PagingInterceptor implements Interceptor {

    @Override
    public Object intercept( Invocation invocation ) throws Throwable {
        Object[] thisArgs = invocation.getArgs();
        MappedStatement mappedStatement = ( MappedStatement ) thisArgs[ 0 ];
        Object unknownArgs = invocation.getArgs()[ 1 ];

        BoundSql boundSql = mappedStatement.getBoundSql( unknownArgs );
        Object result = invocation.proceed();

        if ( result instanceof List ) {
            return new PagingList<>( ( List<Object> ) result, boundSql.getSql(), makeOrderedArgs( boundSql ) );
        }

        if ( result instanceof Set ) {
            return new PagingSet<>( ( Set<Object> ) result, boundSql.getSql(), makeOrderedArgs( boundSql ) );
        }

        if ( result instanceof Map ) {
            return new PagingMap<>( ( Map<Object, Object> ) result, boundSql.getSql(), makeOrderedArgs( boundSql ) );
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

    private Object[] makeOrderedArgs( BoundSql boundSql ) {
        List<ParameterMapping> mappings = boundSql.getParameterMappings();

        if ( 0 == mappings.size() ) {
            return new Object[ 0 ];

        } else if ( 1 == mappings.size() ) {
            return new Object[]{ boundSql.getParameterObject() };
        }

        Object unknownArgs = boundSql.getParameterObject();
        MapperMethod.ParamMap<Object> args;

        if ( unknownArgs instanceof MapperMethod.ParamMap ) {
            args = ( MapperMethod.ParamMap<Object> ) unknownArgs;

        } else {
            throw new IllegalArgumentException( "" );
        }

        List<Object> orderedArgs = new ArrayList<>( mappings.size() );
        for ( ParameterMapping item : mappings ) {
            orderedArgs.add( args.get( item.getProperty() ) );
        }

        return orderedArgs.toArray();
    }
}
