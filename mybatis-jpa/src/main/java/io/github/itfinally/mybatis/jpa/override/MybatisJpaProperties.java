package io.github.itfinally.mybatis.jpa.override;

import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Primary
@Configuration
@ConfigurationProperties( prefix = MybatisProperties.MYBATIS_PREFIX )
public class MybatisJpaProperties extends MybatisProperties {

  @NestedConfigurationProperty
  private MybatisConfiguration configuration;

  @Override
  public MybatisConfiguration getConfiguration() {
    return configuration;
  }

  public MybatisJpaProperties setConfiguration( MybatisConfiguration configuration ) {
    this.configuration = configuration;
    return this;
  }
}
