package top.itfinally.mybatis.jpa.context;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.ResultMapMetadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.*;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/5       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ResultMapContextHolder {
    private static final Logger logger = LoggerFactory.getLogger( ResultMapContextHolder.class );
    private static final Cache<String, ResultMap> resultMaps = CacheBuilder.newBuilder()
            .concurrencyLevel( Runtime.getRuntime().availableProcessors() )
            .maximumSize( 10240 )
            .build();

    private ResultMapContextHolder() {
    }

    public static void resultMapInitializing( Configuration configuration, CrudContextHolder.Context context ) {
        if ( !resultMaps.asMap().containsKey( getResultMapKey( context ) ) ) {
            // prepare cache
            initializingResultMapAndGet( configuration, context );
        }
    }

    public static ResultMap getResultMap( Configuration configuration, CrudContextHolder.Context context ) {
        return initializingResultMapAndGet( configuration, context );
    }

    private static ResultMap initializingResultMapAndGet( final Configuration configuration, final CrudContextHolder.Context context ) {
        try {
            return resultMaps.get( getResultMapKey( context ), new Callable<ResultMap>() {
                @Override
                public ResultMap call() throws Exception {
                    ResultMapMetadata resultMapMetadata = ResultMapXmlContextFactory.buildResultMapMetadata(
                            context.getMethod().getDeclaringClass().getName(), context.getMethod().getName() );

                    if ( !configuration.hasResultMap( resultMapMetadata.getId() ) ) {
                        parseAndGetResultMap( configuration, ResultMapXmlContextFactory.translateMapperXmlAsStream(
                                resultMapMetadata, context.getMetadata() ) );
                    }

                    return configuration.getResultMap( resultMapMetadata.getId() );
                }
            } );

        } catch ( ExecutionException e ) {
            throw new RuntimeException( "Failure to load result map", e );
        }
    }

    private static void parseAndGetResultMap( Configuration configuration, ResultMapMetadata metadata ) throws IOException {
        try ( InputStream in = metadata.getInputStream() ) {
            new XMLMapperBuilder( in, configuration, metadata.getFilePath(), new HashMap<String, XNode>() ).parse();

        } finally {
            if ( new File( metadata.getFilePath() ).delete() ) {
                logger.debug( "Parse done, file '{}' has been deleted", metadata.getFilePath() );

            } else {
                logger.warn( "Parse done, but file '{}' delete failure", metadata.getFilePath() );
            }
        }

        metadata.setInputStream( null );
    }

    private static String getResultMapKey( CrudContextHolder.Context context ) {
        EntityMetadata metadata = context.getMetadata();
        return String.format( "%s.%s", metadata.getEntityClass().getName(), context.getMethod().getName() );
    }
}
