package top.itfinally.mybatis.jpa.criteria.expression;

import top.itfinally.mybatis.jpa.criteria.Predicate;
import top.itfinally.mybatis.jpa.criteria.path.ExpressionImpl;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.query.QueryCollector;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.concurrent.atomic.AtomicBoolean;

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
public abstract class AbstractPredicateImpl extends ExpressionImpl<Boolean> implements Predicate, Writable {

    private final AtomicBoolean reverse = new AtomicBoolean( false );

    protected AbstractPredicateImpl( CriteriaBuilder builder ) {
        super( builder, null );
    }

    @Override
    public boolean isNegated() {
        return reverse.get();
    }

    @Override
    public Predicate not() {
        boolean val = reverse.get();
        reverse.compareAndSet( val, !val );

        return this;
    }
}
