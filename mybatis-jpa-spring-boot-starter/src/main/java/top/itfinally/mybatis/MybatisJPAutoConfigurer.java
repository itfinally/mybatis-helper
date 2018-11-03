package top.itfinally.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import top.itfinally.mybatis.jpa.JpaPrepareInterceptor;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Order
@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty( name = "mybatis.jpa.entity-scan" )
@ComponentScan( basePackages = "top.itfinally.mybatis.jpa" )
@MapperScan( basePackages = "top.itfinally.mybatis.jpa.mapper" )
public class MybatisJPAutoConfigurer implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private JpaPrepareInterceptor jpaPrepareInterceptor;

    @Resource
    private org.apache.ibatis.session.Configuration configuration;

    private final AtomicBoolean isInstalled = new AtomicBoolean( false );

    @Override
    public void onApplicationEvent( ContextRefreshedEvent contextRefreshedEvent ) {
        if ( isInstalled.compareAndSet( false, true ) ) {
            configuration.addInterceptor( jpaPrepareInterceptor );
        }
    }
}
