package top.itfinally.mybatis.generator;

import org.springframework.boot.CommandLineRunner;
import top.itfinally.mybatis.generator.core.database.DatabaseScanComponent;
import top.itfinally.mybatis.generator.core.job.JobGroup;
import top.itfinally.mybatis.generator.core.job.MissionGroupBuilder;
import top.itfinally.mybatis.generator.view.FileBuilder;

import javax.annotation.Resource;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
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
}
