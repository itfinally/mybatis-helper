package top.itfinally.mybatis.jpa.criteria.predicate;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Predicate;
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
 *  v1.0          2018/10/9       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class BooleanAssertionPredicate extends AbstractPredicateImpl implements Predicate {

    private final Expression<?> expression;
    private final boolean assertedValue;

    public BooleanAssertionPredicate( CriteriaBuilder criteriaBuilder, Expression<?> expression, boolean assertedValue ) {
        super( criteriaBuilder );

        this.expression = Objects.requireNonNull( expression, "Expression require not null" );
        this.assertedValue = assertedValue;
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        boolean bool = isNegated() != assertedValue;
        return String.format( "%s is %s true", ( ( Writable ) expression ).toFormatString( parameters ), bool ? "" : "not" );
    }
}
