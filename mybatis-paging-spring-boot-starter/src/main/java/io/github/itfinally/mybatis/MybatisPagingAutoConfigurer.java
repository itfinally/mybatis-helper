package io.github.itfinally.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import io.github.itfinally.mybatis.paging.interceptor.AbstractPagingInterceptor;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@EnableConfigurationProperties
@Order( Ordered.HIGHEST_PRECEDENCE )
@ComponentScan( basePackages = "io.github.itfinally.mybatis.paging" )
public class MybatisPagingAutoConfigurer implements ApplicationListener<ContextRefreshedEvent> {

  @Resource
  private List<SqlSessionFactory> sqlSessionFactories;

  @Resource
  private List<AbstractPagingInterceptor> interceptors;

  private final AtomicBoolean isInstalled = new AtomicBoolean( false );

  @Override
  public void onApplicationEvent( ContextRefreshedEvent contextRefreshedEvent ) {
    if ( isInstalled.compareAndSet( false, true ) ) {
      for ( SqlSessionFactory factory : sqlSessionFactories ) {
        for ( AbstractPagingInterceptor interceptor : interceptors ) {
          factory.getConfiguration().addInterceptor( interceptor );
        }
      }
    }
  }
}
