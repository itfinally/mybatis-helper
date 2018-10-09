package top.itfinally.mybatis.jpa.criteria.predicate;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

import javax.persistence.criteria.Order;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/9       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class OrderImpl implements Order, Writable {

    private final Expression<?> expression;
    private boolean ascending;

    public OrderImpl( Expression<?> expression, boolean ascending ) {
        this.expression = expression;
        this.ascending = ascending;
    }

    @Override
    public Order reverse() {
        ascending = !ascending;
        return null;
    }

    @Override
    public boolean isAscending() {
        return ascending;
    }

    @Override
    public javax.persistence.criteria.Expression<?> getExpression() {
        return null;
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        return String.format( "%s %s", ( ( Writable ) expression ).toFormatString( parameters ), ascending ? "asc" : "desc" );
    }
}
