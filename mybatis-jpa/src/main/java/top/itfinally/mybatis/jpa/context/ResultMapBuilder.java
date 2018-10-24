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
import java.util.*;
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
    private static final String PREFIX = "dynamic_";

    private static volatile ResultMap resultMapWithTypeMap;
    private static final ConcurrentMap<Class<?>, ResultMap> resultMapWithBasicType = new ConcurrentHashMap<>();

    private static final Cache<String, ResultMap> resultMaps = CacheBuilder.newBuilder()
            .concurrencyLevel( Runtime.getRuntime().availableProcessors() )
            .expireAfterWrite( 30, TimeUnit.MINUTES )
            .maximumSize( 51200 )
            .initialCapacity( 128 )
            .weakKeys()
            .build();

    private ResultMapBuilder() {
    }

    public static ResultMap getResultMap( final Configuration configuration, CrudContextHolder.Context context ) {
        final ResultMapToken token = new ResultMapToken( context );
        final EntityMetadata metadata = context.getMetadata();

        try {
            return resultMaps.get( token.getNamespace(), new Callable<ResultMap>() {
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

    public static ResultMap getResultMapWithMapReturned( Configuration configuration ) {
        if ( null == resultMapWithTypeMap ) {
            resultMapWithTypeMap = new ResultMap.Builder( configuration, "", Map.class, new ArrayList<ResultMapping>() ).build();
        }

        return resultMapWithTypeMap;
    }

    public static ResultMap getResultMapWithBasicTypeReturned( Configuration configuration, Class<?> type ) {
        if ( !resultMapWithBasicType.containsKey( type ) ) {
            resultMapWithBasicType.put( type, new ResultMap.Builder( configuration, "", type, new ArrayList<ResultMapping>() ).build() );
        }

        return resultMapWithBasicType.get( type );
    }

    private static class ResultMapToken {
        private static final HashFunction hashFunction = Hashing.hmacMd5( ResultMapToken.class.getName().getBytes() );

        private final String namespace;
        private final String resultMapId;
        private final String hashedCacheKey;

        private ResultMapToken( CrudContextHolder.Context context ) {
            this.namespace = context.getMetadata().getEntityClass().getName();

            this.hashedCacheKey = hashFunction.newHasher().putString( namespace, Charsets.UTF_8 ).hash().toString();

            this.resultMapId = String.format( "%s.%s%s", namespace, PREFIX, hashedCacheKey );
        }

        private String getNamespace() {
            return namespace;
        }

        private String getResultMapId() {
            return resultMapId;
        }

        private String getHashedCacheKey() {
            return hashedCacheKey;
        }
    }

    private static class ResultMapXmlContentBuilder {
        private static String createMapperXmlContent( ResultMapToken token, EntityMetadata metadata ) {
            List<String> sqlSegment = new ArrayList<>();
            List<String> innerResultMap = new ArrayList<>();

            List<String> context = new ArrayList<>( wrapperByResultMapTag( token.getHashedCacheKey(), metadata.getEntityClass(),
                    buildResultMappings( metadata, innerResultMap, sqlSegment, false ) ) );

            if ( !innerResultMap.isEmpty() ) {
                context.add( "\n" );
                context.addAll( innerResultMap );
            }

            if ( !sqlSegment.isEmpty() ) {
                context.add( "\n" );
                context.addAll( sqlSegment );
            }

            List<String> content = wrapperByHeader( wrapperByMapperTag( token.getNamespace(), context ) );

            return Joiner.on( "\n" ).join( content );
        }

        private static List<String> buildResultMappings( EntityMetadata metadata, List<String> innerResultMap,
                                                         List<String> sqlSegment, boolean isIgnoreAssociation ) {

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

            // Because javassist require proxy target object getter / setter method,
            // and there are not getter / setter if using Map or any sub class of Map.
            // Then I think the best way is just mapping the direct relationship, but drop other indirect relationship
            if ( !isIgnoreAssociation ) {
                List<ForeignAttributeMetadata> foreignAttributeMetadata = new ArrayList<>( metadata.getReferenceColumns() );
                Collections.sort( foreignAttributeMetadata, new Comparator<ForeignAttributeMetadata>() {
                    @Override
                    public int compare( ForeignAttributeMetadata left, ForeignAttributeMetadata right ) {
                        return left.isCollection() && right.isCollection() ? 0
                                : left.isCollection() && !right.isCollection() ? 1 : -1;
                    }
                } );

                for ( ForeignAttributeMetadata column : foreignAttributeMetadata ) {
                    localXmlLines.addAll( column.isCollection()
                            ? wrapperByCollectionTag( column, innerResultMap, sqlSegment )
                            : wrapperByAssociationTag( column, innerResultMap, sqlSegment ) );
                }
            }

            return localXmlLines;
        }

        private static String buildIdTag( AttributeMetadata metadata ) {
            return String.format( "<id property=\"%s\" column=\"%s\"/>", metadata.getJavaName(), metadata.getJdbcName() );
        }

        private static String buildResultTag( AttributeMetadata metadata ) {
            return String.format( "<result property=\"%s\" column=\"%s\"/>", metadata.getJavaName(), metadata.getJdbcName() );
        }

        private static List<String> wrapperByAssociationTag( ForeignAttributeMetadata column, List<String> innerResultMap, List<String> sqlSegment ) {
            List<String> localXmlLines = new ArrayList<>();
            String selectMark = String.format( "selectNo_%s", UUID.randomUUID().toString().replaceAll( "-", "" ) );
            String resultMapMark = String.format( "nestResultMap_%s", UUID.randomUUID().toString().replaceAll( "-", "" ) );

            localXmlLines.add( String.format( "<association property=\"%s\" column=\"%s\" select=\"%s\" fetchType=\"%s\"/>",
                    column.getJavaName(), column.getJdbcName(), selectMark, column.isLazy() ? "lazy" : "eager" ) );

            // Add result map declaration if actual type is entity
            Class<?> resultMapType = column.isMap() ? Map.class : column.getEntityMetadata().getEntityClass();

            // Drop indirect relationship if use Map
            innerResultMap.addAll( wrapperByResultMapTag( resultMapMark, resultMapType,
                    buildResultMappings( column.getEntityMetadata(), innerResultMap, sqlSegment, column.isMap() ) ) );

            innerResultMap.add( "\n" );

            sqlSegment.add( String.format( "<select id=\"%s\" resultMap=\"%s\">", selectMark, PREFIX + resultMapMark ) );
            sqlSegment.add( String.format( "select * from %s where %s = #{%s}", column.getEntityMetadata().getTableName(),
                    column.getReferenceAttributeMetadata().getJdbcName(), column.getJavaName() ) );
            sqlSegment.add( "</select>\n" );

            return localXmlLines;
        }

        private static List<String> wrapperByCollectionTag( ForeignAttributeMetadata column, List<String> innerResultMap, List<String> sqlSegment ) {
            List<String> localXmlLines = new ArrayList<>();
            String selectMark = String.format( "selectNo_%s", UUID.randomUUID().toString().replaceAll( "-", "" ) );
            String resultMapMark = String.format( "nestResultMap_%s", UUID.randomUUID().toString().replaceAll( "-", "" ) );

            Class<?> resultMapType = column.isNestMap() ? Map.class : column.getActualType();

            localXmlLines.add( String.format( "<collection property=\"%s\" column=\"%s\" ofType=\"%s\" select=\"%s\" fetchType=\"%s\"/>",
                    column.getJavaName(), column.getJdbcName(), resultMapType.getName(), selectMark, column.isLazy() ? "lazy" : "eager" ) );

            innerResultMap.addAll( wrapperByResultMapTag( resultMapMark, resultMapType,
                    buildResultMappings( column.getEntityMetadata(), innerResultMap, sqlSegment, false ) ) );
            innerResultMap.add( "\n" );

            // Add nest selection
            sqlSegment.add( String.format( "<select id=\"%s\" resultMap=\"%s\">", selectMark, PREFIX + resultMapMark ) );
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
            localXmlLines.add( "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" );
            localXmlLines.addAll( xmlLines );

            return localXmlLines;
        }
    }
}
