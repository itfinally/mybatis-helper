package top.itfinally.mybatis.jpa.criteria.predicate;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.path.ExpressionImpl;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.query.QueryCollector;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

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
public class ValueWrapper extends ExpressionImpl<Object> implements Expression<Object>, Writable {

    private final Object val;

    public ValueWrapper( CriteriaBuilder builder, QueryCollector queryCollector, Object val ) {
        super( builder, queryCollector );

        this.val = val;
    }

    @Override
    public String toString() {
        return val.toString();
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        return val instanceof Expression
                ? ( ( Writable ) val ).toFormatString( parameters )
                : String.format( "#{%s}", parameters.put( val ) );
    }
}
