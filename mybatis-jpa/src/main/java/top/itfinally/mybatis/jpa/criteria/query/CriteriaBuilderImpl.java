package top.itfinally.mybatis.jpa.criteria.query;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Predicate;
import top.itfinally.mybatis.jpa.criteria.predicate.*;

import javax.persistence.criteria.Order;
import java.util.Collection;

import static top.itfinally.mybatis.jpa.criteria.predicate.ComparisonPredicate.Operator;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/1       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class CriteriaBuilderImpl implements CriteriaBuilder {

    @Override
    public CriteriaQuery<Object> createQuery() {
        return new CriteriaQueryImpl<>( this );
    }

    @Override
    public <Entity> Expression<Entity> max( Expression<?> path ) {
        return null;
    }

    @Override
    public <Entity> Expression<Entity> min( Expression<?> path ) {
        return null;
    }

    @Override
    public <Entity> Expression<Entity> count( Expression<?> path ) {
        return null;
    }

    @Override
    public Order asc( Expression<?> expression ) {
        return new OrderImpl( expression, true );
    }

    @Override
    public Order desc( Expression<?> expression ) {
        return new OrderImpl( expression, false );
    }

    @Override
    public Predicate isTrue( Expression<?> expression ) {
        return new BooleanAssertionPredicate( this, expression, true );
    }

    @Override
    public Predicate isFalse( Expression<?> expression ) {
        return new BooleanAssertionPredicate( this, expression, false );
    }

    @Override
    public Predicate isNull( Expression<?> expression ) {
        return new NullnessPredicate( this, expression );
    }

    @Override
    public Predicate isNotNull( Expression<?> expression ) {
        return new NullnessPredicate( this, expression ).not();
    }

    @Override
    public Predicate equal( Expression<?> left, Expression<?> right ) {
        return new ComparisonPredicate( this, Operator.EQUAL, left, right );
    }

    @Override
    public Predicate equal( Expression<?> path, Object value ) {
        return new ComparisonPredicate( this, Operator.EQUAL, path, value );
    }

    @Override
    public Predicate notEqual( Expression<?> left, Expression<?> right ) {
        return new ComparisonPredicate( this, Operator.NOT_EQUAL, left, right );
    }

    @Override
    public Predicate notEqual( Expression<?> path, Object value ) {
        return new ComparisonPredicate( this, Operator.NOT_EQUAL, path, value );
    }

    @Override
    public Predicate greaterThan( Expression<?> left, Expression<?> right ) {
        return new ComparisonPredicate( this, Operator.GREATER_THAN, left, right );
    }

    @Override
    public Predicate greaterThan( Expression<?> path, Object value ) {
        return new ComparisonPredicate( this, Operator.GREATER_THAN, path, value );
    }

    @Override
    public Predicate greaterThanOrEqualTo( Expression<?> left, Expression<?> right ) {
        return new ComparisonPredicate( this, Operator.GREATER_THAN_OR_EQUAL, left, right );
    }

    @Override
    public Predicate greaterThanOrEqualTo( Expression<?> path, Object value ) {
        return new ComparisonPredicate( this, Operator.GREATER_THAN_OR_EQUAL, path, value );
    }

    @Override
    public Predicate lessThan( Expression<?> left, Expression<?> right ) {
        return new ComparisonPredicate( this, Operator.LESS_THAN, left, right );
    }

    @Override
    public Predicate lessThan( Expression<?> path, Object value ) {
        return new ComparisonPredicate( this, Operator.LESS_THAN, path, value );
    }

    @Override
    public Predicate lessThanOrEqualTo( Expression<?> left, Expression<?> right ) {
        return new ComparisonPredicate( this, Operator.LESS_THAN_OR_EQUAL, left, right );
    }

    @Override
    public Predicate lessThanOrEqualTo( Expression<?> path, Object value ) {
        return new ComparisonPredicate( this, Operator.LESS_THAN_OR_EQUAL, path, value );
    }

    @Override
    public Predicate like( Expression<?> path, String pattern ) {
        return null;
    }

    @Override
    public Predicate notLike( Expression<?> path, String pattern ) {
        return null;
    }

    @Override
    public Predicate in( Expression<?> expression, Expression<?> inExpression ) {
        return new InPredicate( this, expression, inExpression );
    }

    @Override
    public Predicate in( Expression<?> expression, Collection<?> values ) {
        return new InPredicate( this, expression, values );
    }
}
