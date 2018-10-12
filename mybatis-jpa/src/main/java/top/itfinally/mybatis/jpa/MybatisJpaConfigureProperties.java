package top.itfinally.mybatis.jpa;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/12       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Configuration
@ConfigurationProperties( prefix = "mybatis.jpa" )
public class MybatisJpaConfigureProperties {

    private String entityScan;

    public String getEntityScan() {
        return entityScan;
    }

    public MybatisJpaConfigureProperties setEntityScan( String entityScan ) {
        this.entityScan = entityScan;
        return this;
    }
}
