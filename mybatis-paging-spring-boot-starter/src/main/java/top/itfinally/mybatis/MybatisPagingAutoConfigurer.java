package top.itfinally.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import top.itfinally.mybatis.paging.interceptor.AbstractPagingInterceptor;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/18       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Configuration
@ComponentScan( "top.itfinally.mybatis.paging" )
public class MybatisPagingAutoConfigurer implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private List<AbstractPagingInterceptor> interceptors;

    @Resource
    private List<SqlSessionFactory> sqlSessionFactories;

    private AtomicBoolean isInstalled = new AtomicBoolean( false );

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
