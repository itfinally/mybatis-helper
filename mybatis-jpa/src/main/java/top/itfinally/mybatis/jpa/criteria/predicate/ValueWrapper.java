package top.itfinally.mybatis.jpa.criteria.predicate;

import top.itfinally.mybatis.jpa.collectors.AbstractCollector;
import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.path.ExpressionImpl;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
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
 *  v1.0          2018/10/4       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ValueWrapper extends ExpressionImpl<Object, AbstractCollector> implements Expression<Object>, Writable {

    private final Object val;

    public ValueWrapper( CriteriaBuilder builder, Object val ) {
        super( builder, null );

        this.val = Objects.requireNonNull( val, "Value require not null" );
    }

    @Override
    protected AbstractCollector queryCollector() {
        throw new UnsupportedOperationException( "Do not calling collector in simple expression." );
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        return val instanceof Expression
                ? ( ( Writable ) val ).toFormatString( parameters )
                : String.format( "#{%s}", parameters.put( val ) );
    }
}
