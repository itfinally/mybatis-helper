package io.github.itfinally.mybatis.jpa.criteria.query;


import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Order;
import io.github.itfinally.mybatis.jpa.criteria.Predicate;

import java.util.Collection;

public interface CriteriaBuilder {
  <Entity> CriteriaQuery<Entity> createCriteriaQuery();

  <Entity> CriteriaUpdate<Entity> createCriteriaUpdate();

  <Entity> CriteriaDelete<Entity> createCriteriaDelete();


  // expression

  <T extends Number> Expression<T> max( Expression<?> path );

  <T extends Number> Expression<T> min( Expression<?> path );

  Expression<Double> avg( Expression<?> path );

  <T extends Number> Expression<T> sum( Expression<?> path );

  Expression<Long> count( Expression<?> path );

  Expression<Long> countDistinct( Expression<?> path );

  <T> Expression<T> function( String name, Object... parameters );

  Order asc( Expression<?> expression );

  Order desc( Expression<?> expression );

  Predicate and( Predicate... predicates );

  Predicate or( Predicate... predicates );

  Predicate isTrue( Expression<?> expression );

  Predicate isFalse( Expression<?> expression );

  Predicate isNull( Expression<?> expression );

  Predicate isNotNull( Expression<?> expression );

  Predicate equal( Expression<?> left, Expression<?> right );

  Predicate equal( Expression<?> path, Object value );

  Predicate notEqual( Expression<?> left, Expression<?> right );

  Predicate notEqual( Expression<?> path, Object value );

  Predicate greaterThan( Expression<?> left, Expression<?> right );

  Predicate greaterThan( Expression<?> path, Object value );

  Predicate greaterThanOrEqualTo( Expression<?> left, Expression<?> right );

  Predicate greaterThanOrEqualTo( Expression<?> path, Object value );

  Predicate lessThan( Expression<?> left, Expression<?> right );

  Predicate lessThan( Expression<?> path, Object value );

  Predicate lessThanOrEqualTo( Expression<?> left, Expression<?> right );

  Predicate lessThanOrEqualTo( Expression<?> path, Object value );

  Predicate like( Expression<?> path, String pattern );

  Predicate notLike( Expression<?> path, String pattern );

  Predicate in( Expression<?> path, Expression<?> expression );

  Predicate in( Expression<?> path, Collection<?> values );
}
