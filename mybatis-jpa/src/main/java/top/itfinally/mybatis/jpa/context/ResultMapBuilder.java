package top.itfinally.mybatis.jpa.context;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import top.itfinally.mybatis.jpa.entity.AttributeMetadata;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.ForeignAttributeMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
public class ResultMapBuilder {
    private static final String PREFIX = "dynamic_method_";

    private static volatile ResultMap resultMapWithTypeMap;

    private static final Cache<String, ResultMap> resultMaps = CacheBuilder.newBuilder()
            .concurrencyLevel( Runtime.getRuntime().availableProcessors() )
            .expireAfterWrite( 30, TimeUnit.MINUTES )
            .maximumSize( 51200 )
            .weakKeys()
            .build();

    private ResultMapBuilder() {
    }

    public static void resultMapInitializing( Configuration configuration, CrudContextHolder.Context context ) {
        ResultMapToken token = new ResultMapToken( context );

        if ( !resultMaps.asMap().containsKey( token.getCacheKey() ) ) {
            // prepare cache
            initializingResultMapAndGet( configuration, token, context.getMetadata() );
        }
    }

    public static ResultMap getResultMap( Configuration configuration, CrudContextHolder.Context context ) {
        return initializingResultMapAndGet( configuration, new ResultMapToken( context ), context.getMetadata() );
    }

    public static ResultMap getResultMapWithMapReturned( Configuration configuration ) {
        if ( null == resultMapWithTypeMap ) {
            resultMapWithTypeMap = new ResultMap.Builder( configuration, "", Map.class, new ArrayList<ResultMapping>() ).build();
        }

        return resultMapWithTypeMap;
    }

    private static ResultMap initializingResultMapAndGet(
            final Configuration configuration, final ResultMapToken token, final EntityMetadata metadata ) {

        try {
            return resultMaps.get( token.getCacheKey(), new Callable<ResultMap>() {
                @Override
                public ResultMap call() throws Exception {
                    if ( !configuration.hasResultMap( token.getResultMapId() ) ) {
                        try ( InputStream in = new ByteArrayInputStream( ResultMapXmlContentBuilder
                                .createMapperXmlContent( token, metadata ).getBytes() ) ) {

                            new XMLMapperBuilder( in, configuration, token.getResultMapId(), configuration.getSqlFragments() ).parse();
                        }
                    }

                    return configuration.getResultMap( token.getResultMapId() );
                }
            } );

        } catch ( ExecutionException e ) {
            throw new RuntimeException( "Failure to load result map", e.getCause() );
        }
    }

    private static class ResultMapToken {
        private static final HashFunction hashFunction = Hashing.hmacMd5( ResultMapToken.class.getName().getBytes() );

        private final String namespace;
        private final String resultMapId;
        private final String cacheKey;
        private final String hashedCacheKey;

        private ResultMapToken( CrudContextHolder.Context context ) {
            Method method = context.getMethod();
            EntityMetadata metadata = context.getMetadata();

            this.namespace = String.format( "%s.%s", method.getDeclaringClass().getName(), method.getName() );

            // the same method sign have different return type through generic type.
            // so combine with the class name of return type and method name as result map unique key
            this.cacheKey = String.format( "%s.%s", metadata.getEntityClass().getName(), context.getMethod().getName() );

            this.hashedCacheKey = hashFunction.newHasher().putString( cacheKey, Charsets.UTF_8 ).hash().toString();

            this.resultMapId = String.format( "%s.%s.%s%s", method.getDeclaringClass().getName(), method.getName(), PREFIX, hashedCacheKey );
        }

        private String getNamespace() {
            return namespace;
        }

        private String getResultMapId() {
            return resultMapId;
        }

        private String getCacheKey() {
            return cacheKey;
        }

        private String getHashedCacheKey() {
            return hashedCacheKey;
        }
    }

    private static class ResultMapXmlContentBuilder {
        private static String createMapperXmlContent( ResultMapToken token, EntityMetadata metadata ) {
            List<String> sqlSegment = new ArrayList<>();
            List<String> results = buildResultMappings( metadata, sqlSegment );
            List<String> resultMap = wrapperByResultMapTag( token.getHashedCacheKey(), metadata.getEntityClass(), results );

            if ( !sqlSegment.isEmpty() ) {
                resultMap.add( "\n" );
                resultMap.addAll( sqlSegment );
            }

            List<String> content = wrapperByHeader( wrapperByMapperTag( token.getNamespace(), resultMap ) );

            return Joiner.on( "\n" ).join( content );
        }

        private static List<String> buildResultMappings( EntityMetadata metadata, List<String> sqlSegment ) {
            List<String> localXmlLines = new ArrayList<>();

            if ( metadata.getId() != null ) {
                localXmlLines.add( buildIdTag( metadata.getId() ) );
            }

            for ( AttributeMetadata column : metadata.getColumns() ) {
                if ( column.isPrimary() ) {
                    continue;
                }

                localXmlLines.add( buildResultTag( column ) );
            }

            for ( ForeignAttributeMetadata column : metadata.getReferenceColumns() ) {
                localXmlLines.addAll( column.isCollection()
                        ? wrapperByCollectionTag( column, sqlSegment )
                        : wrapperByAssociationTag( column, sqlSegment ) );
            }

            return localXmlLines;
        }

        private static String buildIdTag( AttributeMetadata metadata ) {
            return String.format( "<id property=\"%s\" column=\"%s\"/>", metadata.getJavaName(), metadata.getJdbcName() );
        }

        private static String buildResultTag( AttributeMetadata metadata ) {
            return String.format( "<result property=\"%s\" column=\"%s\"/>", metadata.getJavaName(), metadata.getJdbcName() );
        }

        private static List<String> wrapperByAssociationTag( ForeignAttributeMetadata column, List<String> sqlSegment ) {
            List<String> localXmlLines = new ArrayList<>();
            String selectMark = String.format( "selectNo_%s", UUID.randomUUID().toString().replaceAll( "-", "" ) );

            localXmlLines.add( String.format( "<association property=\"%s\" column=\"%s\" select=\"%s\">",
                    column.getJavaName(), column.getJdbcName(), selectMark ) );

            localXmlLines.addAll( buildResultMappings( column.getEntityMetadata(), sqlSegment ) );
            localXmlLines.add( "</association>" );

            // add inner entity selection
            sqlSegment.add( String.format( "<select id=\"%s\">", selectMark ) );
            sqlSegment.add( String.format( "select * from %s where %s = #{%s}", column.getEntityMetadata().getTableName(),
                    column.getReferenceAttributeMetadata().getJdbcName(), column.getJavaName() ) );
            sqlSegment.add( "</select>\n" );

            return localXmlLines;
        }

        private static List<String> wrapperByCollectionTag( ForeignAttributeMetadata column, List<String> sqlSegment ) {
            List<String> localXmlLines = new ArrayList<>();
            String selectMark = String.format( "selectNo_%s", UUID.randomUUID().toString().replaceAll( "-", "" ) );

            localXmlLines.add( String.format( "<collection property=\"%s\" column=\"%s\" ofType=\"%s\" select=\"%s\">",
                    column.getJavaName(), column.getJdbcName(), column.getActualType().getName(), selectMark ) );

            localXmlLines.addAll( buildResultMappings( column.getEntityMetadata(), sqlSegment ) );
            localXmlLines.add( "</collection>" );

            // add inner entity selection
            sqlSegment.add( String.format( "<select id=\"%s\">", selectMark ) );
            sqlSegment.add( String.format( "select * from %s where %s = #{%s}", column.getEntityMetadata().getTableName(),
                    column.getReferenceAttributeMetadata().getJdbcName(), column.getJavaName() ) );
            sqlSegment.add( "</select>\n" );

            return localXmlLines;
        }

        private static List<String> wrapperByResultMapTag( String key, Class<?> type, List<String> xmlLines ) {
            List<String> localXmlLines = new ArrayList<>();

            localXmlLines.add( String.format( "<resultMap id=\"%s%s\" type=\"%s\">", PREFIX, key, type.getName() ) );
            localXmlLines.addAll( xmlLines );
            localXmlLines.add( "</resultMap>" );

            return localXmlLines;
        }

        private static List<String> wrapperByMapperTag( String key, List<String> xmlLines ) {
            List<String> localXmlLines = new ArrayList<>();

            localXmlLines.add( String.format( "<mapper namespace=\"%s\">", key ) );
            localXmlLines.addAll( xmlLines );
            localXmlLines.add( "</mapper>" );

            return localXmlLines;
        }

        private static List<String> wrapperByHeader( List<String> xmlLines ) {
            List<String> localXmlLines = new ArrayList<>();

            localXmlLines.add( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
            localXmlLines.add( "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">" );
            localXmlLines.addAll( xmlLines );

            return localXmlLines;
        }
    }
}
