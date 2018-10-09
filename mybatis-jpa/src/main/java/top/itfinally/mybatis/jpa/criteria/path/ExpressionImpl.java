package top.itfinally.mybatis.jpa.criteria.path;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.query.QueryCollector;
import top.itfinally.mybatis.jpa.criteria.Predicate;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;

import java.util.Collection;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/29       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ExpressionImpl<Entity> extends ReferenceImpl<Entity> implements Expression<Entity> {

    public ExpressionImpl( CriteriaBuilder criteriaBuilder, QueryCollector queryCollector ) {
        super( criteriaBuilder, queryCollector );
    }

    @Override
    public Predicate isNull() {
        return criteriaBuilder().isNull( this );
    }

    @Override
    public Predicate isNotNull() {
        return criteriaBuilder().isNotNull( this );
    }

    @Override
    public Predicate in( Expression<?> value ) {
        return criteriaBuilder().in( this, value );
    }

    @Override
    public Predicate in( Collection<?> values ) {
        return criteriaBuilder().in( this, values );
    }
}
