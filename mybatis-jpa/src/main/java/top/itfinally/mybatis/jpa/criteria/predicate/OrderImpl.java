package top.itfinally.mybatis.jpa.criteria.predicate;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Order;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.Objects;

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
        this.expression = Objects.requireNonNull( expression, "Expression require not null" );
        this.ascending = ascending;
    }

    @Override
    public Order reverse() {
        ascending = !ascending;
        return this;
    }

    @Override
    public boolean isAscending() {
        return ascending;
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        return String.format( "%s %s", ( ( Writable ) expression ).toFormatString( parameters ), ascending ? "asc" : "desc" );
    }
}
