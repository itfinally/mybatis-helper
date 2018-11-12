package top.itfinally.mybatis.generator.view;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import top.itfinally.mybatis.generator.configuration.MybatisGeneratorProperties;
import top.itfinally.mybatis.generator.core.job.JobGroup;
import top.itfinally.mybatis.generator.core.job.JobUnit;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ListIterator;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/3       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Component
public class FileBuilder {
    private Logger logger = LoggerFactory.getLogger( getClass() );

    @Resource
    private MybatisGeneratorProperties properties;

    @Resource
    private TemplateEngine templateEngine;

    public void write( JobGroup jobGroup ) {
        File file = new File( jobGroup.getEntity().getWritePath() );
        if ( !file.exists() || properties.isForceToGenerate() ) {
            String templateName = StringUtils.isEmpty( properties.getEntityTemplateName() )
                    ? "entity.txt" : properties.getEntityTemplateName();

            writeFile( templateName, jobGroup, jobGroup.getEntity() );
        }

        file = new File( jobGroup.getRepository().getWritePath() );
        if ( !file.exists() || properties.isForceToGenerate() ) {
            String templateName = StringUtils.isEmpty( properties.getRepositoryTemplateName() )
                    ? "repository.txt" : properties.getRepositoryTemplateName();

            writeFile( templateName, jobGroup, jobGroup.getRepository() );
        }

        file = new File( jobGroup.getMapperXml().getWritePath() );
        if ( !file.exists() || properties.isForceToGenerate() ) {
            String templateName = StringUtils.isEmpty( properties.getMapperXmlTemplateName() )
                    ? "mapper.txt" : properties.getMapperXmlTemplateName();

            writeFile( templateName, jobGroup, jobGroup.getMapperXml() );
        }

        if ( properties.isIncludeServiceInterfaces() ) {
            file = new File( jobGroup.getServicesInterface().getWritePath() );

            if ( !file.exists() || properties.isForceToGenerate() ) {
                writeFile( properties.getServicesInterfaceTemplateName(), jobGroup, jobGroup.getServicesInterface() );
            }
        }

        if ( properties.isIncludeServices() ) {
            file = new File( jobGroup.getServices().getWritePath() );

            if ( !file.exists() || properties.isForceToGenerate() ) {
                writeFile( properties.getServicesTemplateName(), jobGroup, jobGroup.getServices() );
            }
        }

        if ( properties.isIncludeController() ) {
            file = new File( jobGroup.getController().getWritePath() );

            if ( !file.exists() || properties.isForceToGenerate() ) {
                writeFile( properties.getControllerTemplateName(), jobGroup, jobGroup.getController() );
            }
        }
    }

    private void writeFile( String templateName, JobGroup jobGroup, JobUnit jobUnit ) {
        Context context = new Context();
        context.setVariable( "item", jobUnit );
        context.setVariable( "meta", jobGroup );
        context.setVariable( "properties", properties );
        context.setVariable( "entity", jobGroup.getTableEntity() );

        File target = new File( jobUnit.getWritePath() );

        try {
            if ( isFolderExists( target.getParentFile() ) ) {
                try ( BufferedWriter file = Files.newWriter( target, Charset.forName( "UTF-8" ) ) ) {
                    file.write( mergeBlackLine( jobUnit, templateEngine.process( templateName, context ) ) );
                }
            }

        } catch ( IOException e ) {
            logger.warn( "generate file failure, data table: {}, file path: {}, fail reason: {}",
                    jobGroup.getTableEntity().getJdbcName(), target.getParent(), e.getMessage() );
        }
    }

    private boolean isFolderExists( File path ) {
        if ( !( path.exists() || path.mkdirs() ) ) {
            logger.warn( "create folder failure" );
            return false;
        }

        return true;
    }

    private String mergeBlackLine( JobUnit jobUnit, String content ) {
        List<String> lines = Lists.newArrayList( content.split( "\n" ) );
        ListIterator<String> iterator = lines.listIterator();

        int range = 0;
        String line, delLine;
        while ( iterator.hasNext() ) {
            line = iterator.next();

            if ( line.trim().isEmpty() ) {
                range += 1;
                continue;
            }

            // previous is elems[ --index ], next is elems[ index++ ]
            if ( range > jobUnit.getBlankLine() ) {
                iterator.previous();
                delLine = iterator.previous();

                while ( delLine.trim().isEmpty() ) {
                    iterator.remove();
                    delLine = iterator.previous();
                }

                if ( jobUnit.getBlankLine() > 0 ) {
                    iterator.next();

                    // Re-add the specified number of blank lines
                    for ( int subIndex = 0, subLength = jobUnit.getBlankLine(); subIndex < subLength; subIndex += 1 ) {
                        iterator.add( "" );
                    }
                }
            }

            range = 0;
        }

        return Joiner.on( "\n" ).join( lines );
    }
}
