package top.itfinally.mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.itfinally.mybatis.generator.core.database.DatabaseScanComponent;
import top.itfinally.mybatis.generator.core.database.entity.TableEntity;
import top.itfinally.mybatis.generator.core.job.JobGroup;
import top.itfinally.mybatis.generator.core.job.MissionGroupBuilder;
import top.itfinally.mybatis.generator.MybatisGeneratorRunner;

import javax.annotation.Resource;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@SpringBootApplication
public class ApplicationStarter extends MybatisGeneratorRunner {

    @Resource
    private DatabaseScanComponent.Builder scanComponentBuilder;

    @Resource
    private MissionGroupBuilder groupBuilder;

    public static void main( String[] args ) {
        SpringApplication.run( ApplicationStarter.class, args );
    }

//    @Override
//    public void run( String... strings ) {
//        System.out.println( BasicEntity.class.getGenericSuperclass() );
//        List<TableEntity> entity = scanComponentBuilder.getScanComponent().getTables();
//        List<JobGroup> jobs = groupBuilder.build( entity );
//        System.out.println( jobs );
//    }
}
