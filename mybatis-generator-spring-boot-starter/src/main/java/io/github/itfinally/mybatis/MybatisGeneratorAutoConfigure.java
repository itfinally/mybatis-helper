package io.github.itfinally.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ComponentScan( basePackages = "io.github.itfinally.mybatis.generator" )
@MapperScan( basePackages = "io.github.itfinally.mybatis.generator.core.database.mapper" )
public class MybatisGeneratorAutoConfigure {
}
