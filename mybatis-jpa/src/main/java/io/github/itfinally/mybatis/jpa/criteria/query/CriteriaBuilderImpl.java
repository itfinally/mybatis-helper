package io.github.itfinally.mybatis.jpa.criteria.query;

import com.google.common.collect.Lists;
import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Order;
import io.github.itfinally.mybatis.jpa.criteria.Predicate;
import io.github.itfinally.mybatis.jpa.criteria.predicate.*;

import java.util.Collection;

import static io.github.itfinally.mybatis.jpa.criteria.predicate.ComparisonPredicate.LogicOperation;
import static io.github.itfinally.mybatis.jpa.criteria.predicate.CompoundPredicate.LogicalConjunction;

public class CriteriaBuilderImpl implements CriteriaBuilder {

  @Override
  public <Entity> CriteriaQuery<Entity> createCriteriaQuery() {
    return new CriteriaQueryImpl<>( this );
  }

  @Override
  public <Entity> CriteriaUpdate<Entity> createCriteriaUpdate() {
    return new CriteriaUpdateImpl<>( this );
  }

  @Override
  public <T> CriteriaDelete<T> createCriteriaDelete() {
    return new CriteriaDeleteImpl<>( this );
  }

  @Override
  public <T extends Number> Expression<T> max( Expression<?> path ) {
    return new AggregationFunction.MAX<>( this, path );
  }

  @Override
  public <T extends Number> Expression<T> min( Expression<?> path ) {
    return new AggregationFunction.MIN<>( this, path );
  }

  @Override
  public Expression<Double> avg( Expression<?> path ) {
    return new AggregationFunction.AVG<>( this, path );
  }

  @Override
  public <T extends Number> Expression<T> sum( Expression<?> path ) {
    return new AggregationFunction.SUM<>( this, path );
  }

  @Override
  public Expression<Long> count( Expression<?> path ) {
    return new AggregationFunction.COUNT( this, false, path );
  }

  @Override
  public Expression<Long> countDistinct( Expression<?> path ) {
    return new AggregationFunction.COUNT( this, true, path );
  }

  @Override
  public <T> Expression<T> function( String name, Object... parameters ) {
    return new AggregationFunction<>( this, name, parameters );
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
  public Predicate and( Predicate... predicates ) {
    return new CompoundPredicate( this, LogicalConjunction.AND, Lists.newArrayList( predicates ) );
  }

  @Override
  public Predicate or( Predicate... predicates ) {
    return new CompoundPredicate( this, LogicalConjunction.OR, Lists.newArrayList( predicates ) );
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
    return new ComparisonPredicate( this, LogicOperation.EQUAL, left, right );
  }

  @Override
  public Predicate equal( Expression<?> path, Object value ) {
    return new ComparisonPredicate( this, LogicOperation.EQUAL, path, value );
  }

  @Override
  public Predicate notEqual( Expression<?> left, Expression<?> right ) {
    return new ComparisonPredicate( this, LogicOperation.NOT_EQUAL, left, right );
  }

  @Override
  public Predicate notEqual( Expression<?> path, Object value ) {
    return new ComparisonPredicate( this, LogicOperation.NOT_EQUAL, path, value );
  }

  @Override
  public Predicate greaterThan( Expression<?> left, Expression<?> right ) {
    return new ComparisonPredicate( this, LogicOperation.GREATER_THAN, left, right );
  }

  @Override
  public Predicate greaterThan( Expression<?> path, Object value ) {
    return new ComparisonPredicate( this, LogicOperation.GREATER_THAN, path, value );
  }

  @Override
  public Predicate greaterThanOrEqualTo( Expression<?> left, Expression<?> right ) {
    return new ComparisonPredicate( this, LogicOperation.GREATER_THAN_OR_EQUAL, left, right );
  }

  @Override
  public Predicate greaterThanOrEqualTo( Expression<?> path, Object value ) {
    return new ComparisonPredicate( this, LogicOperation.GREATER_THAN_OR_EQUAL, path, value );
  }

  @Override
  public Predicate lessThan( Expression<?> left, Expression<?> right ) {
    return new ComparisonPredicate( this, LogicOperation.LESS_THAN, left, right );
  }

  @Override
  public Predicate lessThan( Expression<?> path, Object value ) {
    return new ComparisonPredicate( this, LogicOperation.LESS_THAN, path, value );
  }

  @Override
  public Predicate lessThanOrEqualTo( Expression<?> left, Expression<?> right ) {
    return new ComparisonPredicate( this, LogicOperation.LESS_THAN_OR_EQUAL, left, right );
  }

  @Override
  public Predicate lessThanOrEqualTo( Expression<?> path, Object value ) {
    return new ComparisonPredicate( this, LogicOperation.LESS_THAN_OR_EQUAL, path, value );
  }

  @Override
  public Predicate like( Expression<?> path, String pattern ) {
    return new LikePredicate( this, path, pattern );
  }

  @Override
  public Predicate notLike( Expression<?> path, String pattern ) {
    return new LikePredicate( this, path, pattern ).not();
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
