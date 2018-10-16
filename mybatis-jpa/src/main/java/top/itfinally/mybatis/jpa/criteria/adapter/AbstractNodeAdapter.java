package top.itfinally.mybatis.jpa.criteria.adapter;

import top.itfinally.mybatis.jpa.collectors.AbstractCollector;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;

import java.util.Objects;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public abstract class AbstractNodeAdapter<Collector extends AbstractCollector> {
    private final CriteriaBuilder criteriaBuilder;
    private final Collector queryCollector;

    public AbstractNodeAdapter( CriteriaBuilder criteriaBuilder, Collector queryCollector ) {
        this.criteriaBuilder = criteriaBuilder;
        this.queryCollector = queryCollector;
    }

    protected CriteriaBuilder criteriaBuilder() {
        return criteriaBuilder;
    }

    protected Collector queryCollector() {
        return queryCollector;
    }

    protected static <T> T getRealType( Class<T> clazz, Object target ) {
        Objects.requireNonNull( clazz, "Class require not null" );
        Objects.requireNonNull( target, "Target instance require not null" );

        if ( !clazz.isAssignableFrom( target.getClass() ) ) {
            throw new IllegalArgumentException( String.format( "Method expect given type %s but got %s",
                    clazz.getName(), target.getClass().getName() ) );
        }

        return clazz.cast( target );
    }
}
