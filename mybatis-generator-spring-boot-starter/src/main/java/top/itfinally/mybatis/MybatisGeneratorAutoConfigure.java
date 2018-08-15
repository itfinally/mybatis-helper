package top.itfinally.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/11       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Configuration
@EnableConfigurationProperties
@ComponentScan( basePackages = "top.itfinally.mybatis.generator" )
//@MapperScan( basePackages = "top.itfinally.mybatis.generator.core.database.mapper" )
public class MybatisGeneratorAutoConfigure {
}
