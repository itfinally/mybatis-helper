package top.itfinally.mybatis.core;

import com.google.common.base.Joiner;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/31       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class MappedStatementCreator {
    private MappedStatementCreator() {
    }

    public static MappedStatement copy( MappedStatement mappedStatement, BoundSql boundSql ) {
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
}
