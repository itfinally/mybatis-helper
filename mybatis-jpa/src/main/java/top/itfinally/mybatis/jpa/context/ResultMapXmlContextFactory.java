package top.itfinally.mybatis.jpa.context;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.itfinally.mybatis.jpa.entity.AttributeMetadata;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.ResultMapMetadata;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/4       itfinally       首次创建
 * *********************************************
 *
 * 1.
 *
 * </pre>
 */
public class ResultMapXmlContextFactory {
    public static final String PREFIX = "dynamic_method_";

    private static final HashFunction hashFunction = Hashing.hmacMd5( ResultMapXmlContextFactory.class.getName().getBytes() );
    private static final Logger logger = LoggerFactory.getLogger( ResultMapXmlContextFactory.class );
    private static final FileManager fileManager = new FileManager();

    private ResultMapXmlContextFactory() {
    }

    public static ResultMapMetadata buildResultMapMetadata( String className, String methodName ) {
        ResultMapMetadata resultMapMetadata = new ResultMapMetadata().setMethodName( methodName ).setClassName( className );
        return resultMapMetadata.setHashKey( hashFunction.newHasher().putString( resultMapMetadata.toMethodString(), Charsets.UTF_8 ).hash().toString() )
                .setFilePath( fileManager.getOutputFile( String.format( "%s.xml", resultMapMetadata.getHashKey() ) ).getPath() )

                .setId( String.format( "%s.%s.%s%s", className, methodName, PREFIX, resultMapMetadata.getHashKey() ) );
    }

    public static ResultMapMetadata translateMapperXmlAsStream( ResultMapMetadata resultMapMetadata, EntityMetadata metadata ) {
        String filePath = resultMapMetadata.getFilePath();
        String content = createMapperXmlContent( resultMapMetadata, metadata );

        fileManager.write( filePath, createMapperXmlContent( resultMapMetadata, metadata ) );
        logger.debug( "building mapper at '{}';\n content like this:\n\n{}\n\n", filePath, content );

        try {
            return resultMapMetadata.setInputStream( new FileInputStream( filePath ) );

        } catch ( FileNotFoundException e ) {
            throw new RuntimeException( String.format( "Failure to open file input stream for '%s'", filePath ), e );
        }
    }

    private static String createMapperXmlContent( ResultMapMetadata resultMapMetadata, EntityMetadata metadata ) {
        List<String> content = wrapperByHeader(
                wrapperByMapperTag( resultMapMetadata.toMethodString(),
                wrapperByResultMapTag( resultMapMetadata.getHashKey(), metadata.getEntityClass(), buildResultMappings( metadata ) ) ) );

        return Joiner.on( "\n" ).join( content );
    }

    private static List<String> buildResultMappings( EntityMetadata metadata ) {
        List<String> xmlLines = new ArrayList<>();

        if ( metadata.getId() != null ) {
            xmlLines.add( buildIdTag( metadata.getId() ) );
        }

        for ( AttributeMetadata column : metadata.getColumns() ) {
            if ( column.isPrimary() ) {
                continue;
            }

            xmlLines.add( buildResultTag( column ) );

            // todo 这里还缺少对关联标签的处理
        }

        return xmlLines;
    }

    private static String buildIdTag( AttributeMetadata metadata ) {
        return String.format( "<id property=\"%s\" column=\"%s\"/>", metadata.getJavaName(), metadata.getJdbcName() );
    }

    private static String buildResultTag( AttributeMetadata metadata ) {
        return String.format( "<result property=\"%s\" column=\"%s\"/>", metadata.getJavaName(), metadata.getJdbcName() );
    }

    private static List<String> wrapperByAssociationTag( String property, String column, List<String> xmlLines ) {
        return xmlLines;
    }

    private static List<String> wrapperByCollectionTag( String property, String column, List<String> xmlLines ) {
        return xmlLines;
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

    private static class FileManager {
        private static final String MAPPER_FOLDER_NAME = "mapper";
        private static final String SYSTEM_TEMPORARY_FOLDER_PATH = System.getProperty( "java.io.tmpdir" );
        private static final String DEFAULT_TEMPORARY_FOLDER_PATH = Objects.requireNonNull( Thread.currentThread()
                .getContextClassLoader().getResource( "." ) ).getPath();

        private String write( String path, String content ) {
            try ( OutputStream out = getOutputStream( path ) ) {
                out.write( content.getBytes() );
                out.flush();

            } catch ( IOException e ) {
                throw new RuntimeException( String.format( "Failure to write file '%s', method: %s", path, e.getMessage() ), e );
            }

            return content;
        }

        private OutputStream getOutputStream( String path ) {
            try {
                return new FileOutputStream( path );

            } catch ( FileNotFoundException e ) {
                throw new RuntimeException( String.format( "Failure to open output stream with path '%s', message: %s",
                        path, e.getMessage() ), e );
            }
        }

        private File getOutputFile( String fileName ) {
            File folder = new File( SYSTEM_TEMPORARY_FOLDER_PATH );
            String folderPath = folder.exists() || folder.mkdirs() ? SYSTEM_TEMPORARY_FOLDER_PATH : DEFAULT_TEMPORARY_FOLDER_PATH;
            return Paths.get( folderPath, MAPPER_FOLDER_NAME, fileName ).toFile();
        }
    }
}
