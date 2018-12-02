package io.github.itfinally.mybatis.jpa;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
