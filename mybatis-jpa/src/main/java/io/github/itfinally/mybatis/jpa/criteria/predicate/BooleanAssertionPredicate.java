package io.github.itfinally.mybatis.jpa.criteria.predicate;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Predicate;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.Objects;

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
