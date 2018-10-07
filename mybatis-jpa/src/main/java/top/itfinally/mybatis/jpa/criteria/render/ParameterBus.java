package top.itfinally.mybatis.jpa.criteria.render;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/4       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ParameterBus extends ConcurrentHashMap<String, Object> implements ConcurrentMap<String, Object> {

    private static final String PREFIX = "jpa-parameter-%s";

    private final AtomicInteger step = new AtomicInteger( 0 );

    public String put( Object value ) {
        int index = step.getAndIncrement();
        String key = String.format( PREFIX, index );

        put( key, value );

        return key;
    }
}
