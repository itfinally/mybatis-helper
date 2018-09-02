package top.itfinally.mybatis.paging.interceptor;

import net.sf.jsqlparser.JSQLParserException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.springframework.jdbc.core.JdbcTemplate;
import top.itfinally.mybatis.core.MappedStatementCreator;
import top.itfinally.mybatis.core.MybatisCoreConfiguration;
import top.itfinally.mybatis.paging.Pager;
import top.itfinally.mybatis.paging.PagingItem;
import top.itfinally.mybatis.paging.collection.PagingCursor;
import top.itfinally.mybatis.paging.collection.PagingList;
import top.itfinally.mybatis.paging.collection.PagingMap;
import top.itfinally.mybatis.paging.collection.PagingSet;
import top.itfinally.mybatis.paging.configuration.MybatisPagingProperties;
import top.itfinally.mybatis.paging.interceptor.hook.MySqlHook;
import top.itfinally.mybatis.paging.interceptor.hook.SqlHook;

import java.util.*;

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
@SuppressWarnings( "unchecked" )
public abstract class AbstractPagingInterceptor implements Interceptor {
    private final String databaseId;
    private final JdbcTemplate jdbcTemplate;

    private MybatisPagingProperties properties;

    AbstractPagingInterceptor( MybatisPagingProperties properties ) {
        this.properties = properties;

        databaseId = properties.getDatabaseId();
        jdbcTemplate = new JdbcTemplate( properties.getDatasource() );
    }

    protected abstract void hook( Object[] thisArgs, MappedStatement mappedStatement, BoundSql boundSql );

    @Override
    public Object intercept( Invocation invocation ) throws Throwable {
        PagingItem pagingItem = PagingLocal.getPagingItem();

        if ( null == pagingItem ) {
            return invocation.proceed();
        }

        if ( !pagingItem.isHolding() ) {
            Pager.clear();
        }

        Object[] thisArgs = invocation.getArgs();
        MappedStatement mappedStatement = ( MappedStatement ) thisArgs[ 0 ];
        Object unknownArgs = thisArgs[ 1 ];

        BoundSql boundSql = mappedStatement.getBoundSql( unknownArgs );
        SqlHook sqlHook = getSqlHook( boundSql.getSql(), pagingItem );

        boundSql = new BoundSql( mappedStatement.getConfiguration(), sqlHook.getPagingSql(),
                boundSql.getParameterMappings(), boundSql.getParameterObject() );

        hook( thisArgs, MappedStatementCreator.copy( mappedStatement, boundSql ), boundSql );

        Object result = invocation.proceed();

        if ( result instanceof List ) {
            return new PagingList<>( ( List<Object> ) result, pagingItem, sqlHook.getCountingSql(),
                    makeOrderedArgs( boundSql ), jdbcTemplate, properties.getIndexStartingWith() );
        }

        if ( result instanceof Set ) {
            return new PagingSet<>( ( Set<Object> ) result, pagingItem, sqlHook.getCountingSql(),
                    makeOrderedArgs( boundSql ), jdbcTemplate, properties.getIndexStartingWith() );
        }

        if ( result instanceof Map ) {
            return new PagingMap<>( ( Map<Object, Object> ) result, pagingItem, sqlHook.getCountingSql(),
                    makeOrderedArgs( boundSql ), jdbcTemplate, properties.getIndexStartingWith() );
        }

        if ( result instanceof Cursor ) {
            return new PagingCursor<>( ( Cursor<Object> ) result, pagingItem, sqlHook.getCountingSql(),
                    makeOrderedArgs( boundSql ), jdbcTemplate, properties.getIndexStartingWith() );
        }

        return result;
    }

    @Override
    public Object plugin( Object target ) {
        return Plugin.wrap( target, this );
    }

    @Override
    public void setProperties( Properties properties ) {
    }

    private static Object[] makeOrderedArgs( BoundSql boundSql ) {
        List<ParameterMapping> mappings = boundSql.getParameterMappings();

        if ( mappings.isEmpty() ) {
            return new Object[ 0 ];
        }

        Object unknownArgs = boundSql.getParameterObject();
        MapperMethod.ParamMap<Object> args;

        if ( unknownArgs instanceof MapperMethod.ParamMap ) {
            args = ( MapperMethod.ParamMap<Object> ) unknownArgs;

        } else {
            throw new IllegalArgumentException( String.format( "Mybatis given unexpected parameters from boundSql.getParameterObject(), " +
                    "type: %s", unknownArgs.getClass().getName() ) );
        }

        List<Object> orderedArgs = new ArrayList<>( mappings.size() );
        for ( ParameterMapping item : mappings ) {
            orderedArgs.add( args.get( item.getProperty() ) );
        }

        return orderedArgs.toArray();
    }

    private SqlHook getSqlHook( String sql, PagingItem pagingItem ) {
        try {
            if ( properties != null && properties.getSqlHookMap().containsKey( sql.toLowerCase() ) ) {
                SqlHook sqlHook = properties.getSqlHookMap().get( sql.toLowerCase() )
                        .build( sql, pagingItem.getBeginRow(), pagingItem.getRange() );

                if ( sqlHook != null ) {
                    return sqlHook;
                }
            }

            switch ( databaseId ) {
                case MybatisCoreConfiguration.MYSQL: {
                    return new MySqlHook.Builder().build( sql, pagingItem.getBeginRow(), pagingItem.getRange() );
                }

                default: {
                    throw new UnsupportedOperationException( String.format( "Unsupported database '%s'", databaseId ) );
                }
            }

        } catch ( JSQLParserException e ) {
            throw new IllegalStateException( String.format( "Failure to parse sql, SQL: %s", sql ), e );
        }
    }
}
