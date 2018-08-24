package top.itfinally.mybatis.jpa.entity;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

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
public class EntityScanner implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent( ContextRefreshedEvent contextRefreshedEvent ) {
        
    }
}
