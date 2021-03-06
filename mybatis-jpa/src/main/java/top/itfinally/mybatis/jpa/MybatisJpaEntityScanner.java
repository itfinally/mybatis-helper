package top.itfinally.mybatis.jpa;


import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.itfinally.mybatis.jpa.context.MetadataFactory;

import javax.persistence.Table;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/11       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class MybatisJpaEntityScanner {
    private static final Logger logger = LoggerFactory.getLogger( MybatisJpaEntityScanner.class );

    public void scan( MybatisJpaConfigureProperties properties ) {
        if ( Strings.isNullOrEmpty( properties.getEntityScan() ) ) {
            return;
        }

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String[] entityScanClassPaths = properties.getEntityScan().split( "," );
        List<String> classNames = new ArrayList<>();

        try {
            for ( String classPath : entityScanClassPaths ) {

                classPath = classPath.trim();

                if ( Strings.isNullOrEmpty( classPath ) ) {
                    continue;
                }

                logger.info( "Start to scanning class path: {}", classPath );

                URL url = classLoader.getResource( classPath.replaceAll( "\\.", File.separator ) );

                if ( null == url ) {
                    throw new RuntimeException( new FileNotFoundException( String.format(
                            "No package found for route: '%s'", properties.getEntityScan() ) ) );
                }

                File folder = Paths.get( url.toURI() ).toFile();
                if ( !folder.exists() ) {
                    throw new RuntimeException( new FileNotFoundException( String.format( "Path is not exist: '%s'", folder.getPath() ) ) );
                }

                walkFolder( classPath, classNames, new ArrayDeque<>( Collections.singletonList( folder ) ) );
            }

        } catch ( URISyntaxException e ) {
            throw new RuntimeException( "Scanning class file failure", e );
        }

        List<Class<?>> classes = new ArrayList<>();
        Class<?> clazz;

        try {
            for ( String className : classNames ) {
                clazz = Class.forName( className, false, classLoader );

                if ( clazz.getAnnotation( Table.class ) != null ) {
                    classes.add( clazz );
                }
            }

        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }

        MetadataFactory.hittingMetadata( classes );
    }

    private List<String> walkFolder( String classPath, List<String> classNames, Deque<File> folders ) {
        File folder = folders.pollFirst();
        if ( null == folder ) {
            return classNames;
        }

        File[] files = folder.listFiles();
        if ( null == files ) {
            return walkFolder( classPath, classNames, folders );
        }

        String className;
        for ( File item : files ) {
            if ( item.isFile() ) {
                className = extractClassName( classPath, item.getPath() );

                if ( !Strings.isNullOrEmpty( className ) ) {
                    classNames.add( className );
                }
            }

            if ( item.isDirectory() ) {
                folders.offerLast( item );
            }
        }

        return walkFolder( classPath, classNames, folders );
    }

    private String extractClassName( String classPath, String path ) {
        String subPath = path.split( classPath.replaceAll( "\\.", File.separator ) )[ 1 ];
        String subClassPath = subPath.replace( ".class", "" ).replaceAll( File.separator, "." );

        // no including inner class
        return subClassPath.contains( "$" ) ? "" : String.format( "%s%s", classPath, subClassPath );
    }
}
