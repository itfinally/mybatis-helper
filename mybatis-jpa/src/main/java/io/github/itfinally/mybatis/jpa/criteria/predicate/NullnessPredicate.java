package io.github.itfinally.mybatis.jpa.criteria.predicate;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Predicate;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.Objects;

public class NullnessPredicate extends AbstractPredicateImpl implements Predicate {

  private final Expression<?> expression;

  public NullnessPredicate( CriteriaBuilder builder, Expression<?> expression ) {
    super( builder );

    this.expression = Objects.requireNonNull( expression, "Expression require not null" );
  }

  @Override
  public String toFormatString( ParameterBus parameters ) {
    return String.format( "%s is %s null", ( ( Writable ) expression ).toFormatString( parameters ), isNegated() ? "not" : "" );
  }
}
