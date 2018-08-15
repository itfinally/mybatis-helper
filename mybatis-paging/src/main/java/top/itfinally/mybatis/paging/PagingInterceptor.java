package top.itfinally.mybatis.paging;

import com.google.common.base.Joiner;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.jdbc.core.JdbcTemplate;
import top.itfinally.mybatis.paging.collection.PagingList;
import top.itfinally.mybatis.paging.collection.PagingMap;
import top.itfinally.mybatis.paging.collection.PagingSet;
import top.itfinally.mybatis.paging.hook.MySqlHook;
import top.itfinally.mybatis.paging.hook.SqlHook;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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
@SuppressWarnings( "unchecked" )
@Intercepts( @Signature( type = Executor.class, method = "query",
        args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class } ) )
public class PagingInterceptor implements Interceptor {
    private final String databaseId;
    private final JdbcTemplate jdbcTemplate;

    public PagingInterceptor( DataSource dataSource ) {
        try ( Connection connection = dataSource.getConnection() ) {
            databaseId = connection.getMetaData().getDatabaseProductName().toLowerCase();
            jdbcTemplate = new JdbcTemplate( dataSource );

        } catch ( SQLException e ) {
            throw new RuntimeException( "Cannot getting database name.", e );
        }
    }

    @Override
    public Object intercept( Invocation invocation ) throws Throwable {
        PagingItem pagingItem = PagingExecutor.getPagingItem();

        if ( null == pagingItem ) {
            return invocation.proceed();
        }

        if ( !pagingItem.isHolding() ) {
            PagingExecutor.clear();
        }

        Object[] thisArgs = invocation.getArgs();
        MappedStatement mappedStatement = ( MappedStatement ) thisArgs[ 0 ];
        Object unknownArgs = thisArgs[ 1 ];

        BoundSql boundSql = mappedStatement.getBoundSql( unknownArgs );
        SqlHook sqlHook = getSqlHook( boundSql.getSql(), pagingItem );

        boundSql = new BoundSql( mappedStatement.getConfiguration(), sqlHook.getPagingSql(),
                boundSql.getParameterMappings(), boundSql.getParameterObject() );

        thisArgs[ 0 ] = copyMappedStatement( mappedStatement, boundSql );
        Object result = invocation.proceed();

        if ( result instanceof List ) {
            return new PagingList<>( ( List<Object> ) result, pagingItem, sqlHook.getCountingSql(), makeOrderedArgs( boundSql ), jdbcTemplate );
        }

        if ( result instanceof Set ) {
            return new PagingSet<>( ( Set<Object> ) result, pagingItem, sqlHook.getCountingSql(), makeOrderedArgs( boundSql ), jdbcTemplate );
        }

        if ( result instanceof Map ) {
            return new PagingMap<>( ( Map<Object, Object> ) result, pagingItem, sqlHook.getCountingSql(), makeOrderedArgs( boundSql ), jdbcTemplate );
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

    private MappedStatement copyMappedStatement( MappedStatement mappedStatement, BoundSql boundSql ) {
        StaticSqlSource sqlSource = new StaticSqlSource( mappedStatement.getConfiguration(), boundSql.getSql(), boundSql.getParameterMappings() );
        MappedStatement.Builder builder = new MappedStatement.Builder( mappedStatement.getConfiguration(),
                mappedStatement.getId(), sqlSource, mappedStatement.getSqlCommandType() );

        if ( mappedStatement.getKeyColumns() != null && mappedStatement.getKeyColumns().length > 0 ) {
            builder.keyColumn( Joiner.on( "," ).join( mappedStatement.getKeyColumns() ) );
        }

        if ( mappedStatement.getKeyProperties() != null && mappedStatement.getKeyProperties().length > 0 ) {
            builder.keyProperty( Joiner.on( "," ).join( mappedStatement.getKeyProperties() ) );
        }

        if ( mappedStatement.getResultSets() != null && mappedStatement.getResultSets().length > 0 ) {
            builder.resultSets( Joiner.on( "," ).join( mappedStatement.getResultSets() ) );
        }

        return builder.cache( mappedStatement.getCache() )
                .fetchSize( mappedStatement.getFetchSize() )
                .databaseId( mappedStatement.getDatabaseId() )
                .keyGenerator( mappedStatement.getKeyGenerator() )
                .flushCacheRequired( mappedStatement.isFlushCacheRequired() )

                .lang( mappedStatement.getLang() )
                .timeout( mappedStatement.getTimeout() )
                .useCache( mappedStatement.isUseCache() )
                .resource( mappedStatement.getResource() )
                .resultMaps( mappedStatement.getResultMaps() )

                .parameterMap( mappedStatement.getParameterMap() )
                .resultOrdered( mappedStatement.isResultOrdered() )
                .resultSetType( mappedStatement.getResultSetType() )
                .statementType( mappedStatement.getStatementType() )

                .build();
    }

    private SqlHook getSqlHook( String sql, PagingItem pagingItem ) {
        try {
            switch ( databaseId ) {
                case "mysql": {
                    return new MySqlHook( sql, pagingItem.getBeginRow(), pagingItem.getRange() );
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
