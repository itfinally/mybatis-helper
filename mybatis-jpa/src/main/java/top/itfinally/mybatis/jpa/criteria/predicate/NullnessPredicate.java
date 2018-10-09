package top.itfinally.mybatis.jpa.criteria.predicate;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Predicate;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

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
public class NullnessPredicate extends AbstractPredicateImpl implements Predicate {

    private final Expression<?> expression;

    public NullnessPredicate( CriteriaBuilder builder, Expression<?> expression ) {
        super( builder );

        this.expression = expression;
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        return String.format( "%s is %s null", ( ( Writable ) expression ).toFormatString( parameters ), isNegated() ? "not" : "" );
    }
}
