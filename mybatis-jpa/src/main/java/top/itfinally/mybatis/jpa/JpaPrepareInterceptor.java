package top.itfinally.mybatis.jpa;

import com.google.common.collect.Lists;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import top.itfinally.mybatis.core.MappedStatementCreator;
import top.itfinally.mybatis.core.MybatisCoreConfiguration;
import top.itfinally.mybatis.jpa.context.ResultMapContextHolder;
import top.itfinally.mybatis.jpa.sql.BasicCrudSqlCreator;
import top.itfinally.mybatis.jpa.context.CrudContextHolder;
import top.itfinally.mybatis.jpa.sql.MysqlCrudSqlCreator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/24       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Component
@Intercepts( {
        @Signature( type = Executor.class, method = "update", args = { MappedStatement.class, Object.class } ),
        @Signature( type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class } ),
        @Signature( type = Executor.class, method = "queryCursor", args = { MappedStatement.class, Object.class, RowBounds.class } ),
        @Signature( type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class } )
} )
public class JpaPrepareInterceptor implements Interceptor {
    private final BasicCrudSqlCreator sqlCreator;

    private final Map<String, Class<?>[]> parameters;
    private final Map<String, Method> methods;

    public JpaPrepareInterceptor( MybatisJpaConfig jpaConfig, ApplicationContext context ) {
        switch ( jpaConfig.getDatabaseId() ) {
            case MybatisCoreConfiguration.MYSQL: {
                sqlCreator = context.getBean( MysqlCrudSqlCreator.class );
                break;
            }

            default: {
                throw new UnsupportedOperationException( String.format( "No match database '%s'", jpaConfig.getDatabaseId() ) );
            }
        }

        Map<String, Method> methods = new HashMap<>();
        Map<String, Class<?>[]> parameters = new HashMap<>();
        for ( Method method : sqlCreator.getClass().getDeclaredMethods() ) {
            methods.put( method.getName(), method );
            parameters.put( method.getName(), method.getParameterTypes() );
        }

        this.methods = Collections.unmodifiableMap( methods );
        this.parameters = Collections.unmodifiableMap( parameters );
    }

    @Override
    public Object intercept( Invocation invocation ) throws Throwable {
        CrudContextHolder.Context context = CrudContextHolder.getContext();
        if ( null == context ) {
            return invocation.proceed();
        }

        CrudContextHolder.clear();

        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = ( MappedStatement ) args[ 0 ];
        Object unknownArgs = args[ 1 ];

        MappedStatement.Builder mappedStatementBuilder = MappedStatementCreator
                .getMappedStatementBuilder( mappedStatement, getBoundSql( context, unknownArgs ) );

        if ( mappedStatement.getSqlCommandType() == SqlCommandType.SELECT ) {
            ResultMap resultMap = ResultMapContextHolder.getResultMap( mappedStatement.getConfiguration(), context );
            mappedStatementBuilder.resultMaps( Lists.newArrayList( resultMap ) );
        }


        args[ 0 ] = mappedStatementBuilder.build();

        return invocation.proceed();
    }

    @Override
    public Object plugin( Object target ) {
        return Plugin.wrap( target, this );
    }

    @Override
    public void setProperties( Properties properties ) {
    }

    private BoundSql getBoundSql( CrudContextHolder.Context context, Object unknownArgs ) {
        try {
            if ( !methods.containsKey( context.getMethod().getName() ) ) {
                throw new RuntimeException( new NoSuchMethodException( String.format(
                        "Failure to getting method '%s' from sql creator", context.getMethod().getName() ) ) );
            }

            Method method = methods.get( context.getMethod().getName() );
            switch ( parameters.get( method.getName() ).length ) {
                case 1: {
                    return ( BoundSql ) method.invoke( sqlCreator, context.getMetadata() );
                }

                case 2: {
                    return ( BoundSql ) method.invoke( sqlCreator, context.getMetadata(), unknownArgs );
                }

                default: {
                    throw new UnsupportedOperationException( String.format( "method: %s parameter length: %d not found",
                            method.getName(), parameters.get( method.getName() ).length ) );
                }
            }

        } catch ( IllegalAccessException | InvocationTargetException e ) {
            throw new RuntimeException( String.format( "Failure to invoke method '%s' by sqlSource object",
                    context.getMethod().getName() ), e );
        }
    }
}
