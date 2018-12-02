package io.github.itfinally.mybatis.jpa.criteria.predicate;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Predicate;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.Objects;

public class LikePredicate extends AbstractPredicateImpl implements Predicate {

  private final Expression<?> expression;
  private final Object unknownPattern;

  public LikePredicate( CriteriaBuilder builder, Expression<?> expression, Object unknownPattern ) {
    super( builder );

    this.expression = Objects.requireNonNull( expression, "Expression require not null" );
    this.unknownPattern = Objects.requireNonNull( unknownPattern, "Pattern require not null" );
  }

  @Override
  public String toFormatString( ParameterBus parameters ) {
    String likeClause;

    if ( unknownPattern instanceof String ) {
      likeClause = String.format( "#{%s}", parameters.put( ( ( String ) unknownPattern ).trim() ) );

    } else if ( unknownPattern instanceof Expression ) {
      likeClause = ( ( Writable ) unknownPattern ).toFormatString( parameters );

    } else {
      throw new IllegalArgumentException( "LikePredicate have an unknown type of parameter: " + unknownPattern.getClass().getName() );
    }

    return String.format( "%s %s like %s", ( ( Writable ) expression ).toFormatString( parameters ),
        isNegated() ? "not" : "", likeClause );
  }
}
