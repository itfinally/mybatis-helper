package top.itfinally.mybatis.jpa.criteria.render;

import java.util.*;
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
 *  v1.0          2018/10/5       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class OrderConcurrentHashMap<V> extends ConcurrentHashMap<String, OrderConcurrentHashMap.OrderWrapper<V>>
        implements ConcurrentMap<String, OrderConcurrentHashMap.OrderWrapper<V>> {

    private final AtomicInteger counter = new AtomicInteger();

    public V orderPut( String key, V value ) {
        OrderWrapper<V> orderWrapper = putIfAbsent( key, new OrderWrapper<>( counter.getAndIncrement(), value ) );
        return null == orderWrapper ? null : orderWrapper.value;
    }

    public List<V> orderValues() {
        List<OrderWrapper<V>> wrappers = ( List<OrderWrapper<V>> ) values();

        Collections.sort( wrappers, new Comparator<OrderWrapper<V>>() {
            @Override
            public int compare( OrderWrapper<V> o1, OrderWrapper<V> o2 ) {
                return Integer.compare( o1.index, o2.index );
            }
        } );

        List<V> values = new ArrayList<>( wrappers.size() );
        for ( OrderWrapper<V> item : wrappers ) {
            values.add( item.value );
        }

        return values;
    }

    public static class OrderWrapper<V> {
        private final int index;
        private final V value;

        public OrderWrapper( int index, V value ) {
            Objects.requireNonNull( value );

            this.index = index;
            this.value = value;
        }

        public V getValue() {
            return value;
        }

        @Override
        @SuppressWarnings( "all" )
        public boolean equals( Object o ) {
            return value.equals( o );
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}
