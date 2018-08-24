package top.itfinally.mybatis.jpa.override;

import org.apache.ibatis.session.ExecutorType;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Properties;

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
@ConfigurationProperties( prefix = "mybatis" )
public class MybatisJpaProperties extends MybatisProperties {
    private org.apache.ibatis.session.Configuration configuration;

    public MybatisJpaProperties( org.apache.ibatis.session.Configuration configuration ) {
        this.configuration = configuration;
    }

    @Override
    public org.apache.ibatis.session.Configuration getConfiguration() {
        return configuration;
    }
}
