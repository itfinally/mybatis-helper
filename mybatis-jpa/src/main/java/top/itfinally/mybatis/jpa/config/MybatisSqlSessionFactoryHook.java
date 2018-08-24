package top.itfinally.mybatis.jpa.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.itfinally.mybatis.jpa.mapper.BasicCrudActor;
import top.itfinally.mybatis.jpa.override.MybatisJpaConfiguration;

import javax.sql.DataSource;
import java.lang.reflect.Field;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/24       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Configuration
public class MybatisSqlSessionFactoryHook {

    // 在查看各种资料和源码后, 实在找不到比较优雅的方法去替换 SqlSessionFactory 内部的 configuration 属性
    // 只能用反射暴力替换该属性, 以达到注入自定义代理实现的目的
//    public MybatisSqlSessionFactoryHook( SqlSessionFactory factory ) {
//        if ( factory.getConfiguration().getClass() == MybatisJpaConfiguration.class ) {
//            return;
//        }
//
//        for ( Field field : factory.getClass().getDeclaredFields() ) {
//            if ( field.getType() != org.apache.ibatis.session.Configuration.class ) {
//                continue;
//            }
//
//            try {
//                field.setAccessible( true );
//                field.set( factory, new MybatisJpaConfiguration( factory.getConfiguration() ) );
//
//            } catch ( IllegalAccessException e ) {
//                throw new RuntimeException( "Failure to replace configuration for SqlSessionFactory", e );
//
//            } finally {
//                field.setAccessible( false );
//            }
//        }
//    }

    @Bean
    public MybatisProperties mybatisProperties() {
        MybatisProperties localProperties = new MybatisProperties();
        localProperties.setConfiguration( new MybatisJpaConfiguration() );

        return localProperties;
    }
}
