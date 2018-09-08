package top.itfinally.mybatis.jpa.override;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/25       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Primary
@Configuration
@ConfigurationProperties( prefix = MybatisProperties.MYBATIS_PREFIX )
public class MybatisJpaProperties extends MybatisProperties {

    @NestedConfigurationProperty
    private MybatisJpaConfiguration configuration;

    @Override
    public MybatisJpaConfiguration getConfiguration() {
        return configuration;
    }

    public MybatisJpaProperties setConfiguration( MybatisJpaConfiguration configuration ) {
        this.configuration = configuration;
        return this;
    }
}
