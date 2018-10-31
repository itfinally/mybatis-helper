package top.itfinally.mybatis.generator;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import top.itfinally.mybatis.generator.core.database.DatabaseScanComponent;
import top.itfinally.mybatis.generator.core.job.JobGroup;
import top.itfinally.mybatis.generator.core.job.MissionGroupBuilder;
import top.itfinally.mybatis.generator.view.FileBuilder;

import javax.annotation.Resource;

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
public class MybatisGeneratorRunner implements CommandLineRunner {

    @Resource
    private DatabaseScanComponent.Builder scanComponentBuilder;

    @Resource
    private MissionGroupBuilder groupBuilder;

    @Resource
    private FileBuilder fileBuilder;

    @Override
    public void run( String... strings ) {
        for ( JobGroup item : groupBuilder.build( scanComponentBuilder.getScanComponent().getTables() ) ) {
            fileBuilder.write( item );
        }
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode( TemplateMode.TEXT );
        templateResolver.setCharacterEncoding( "UTF-8" );
        templateResolver.setCacheable( false );

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver( templateResolver );
        return templateEngine;
    }
}
