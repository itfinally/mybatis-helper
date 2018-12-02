package io.github.itfinally.mybatis.jpa.criteria.path;

import io.github.itfinally.mybatis.jpa.collectors.AbstractCollector;
import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Predicate;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;

import java.util.Collection;

public class ExpressionImpl<Value, Collector extends AbstractCollector> extends ReferenceImpl<Value, Collector>
    implements Expression<Value> {

  public ExpressionImpl( CriteriaBuilder criteriaBuilder, Collector queryCollector ) {
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
