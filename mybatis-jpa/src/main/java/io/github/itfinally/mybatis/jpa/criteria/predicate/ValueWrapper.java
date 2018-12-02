package io.github.itfinally.mybatis.jpa.criteria.predicate;

import io.github.itfinally.mybatis.jpa.collectors.AbstractCollector;
import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.path.ExpressionImpl;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.Objects;

public class ValueWrapper extends ExpressionImpl<Object, AbstractCollector> implements Expression<Object>, Writable {

  private final Object val;

  public ValueWrapper( CriteriaBuilder builder, Object val ) {
    super( builder, null );

    this.val = Objects.requireNonNull( val, "Value require not null" );
  }

  @Override
  protected AbstractCollector queryCollector() {
    throw new UnsupportedOperationException( "Do not calling collector in simple expression." );
  }

  @Override
  public String toFormatString( ParameterBus parameters ) {
    return val instanceof Expression
        ? ( ( Writable ) val ).toFormatString( parameters )
        : String.format( "#{%s}", parameters.put( val ) );
  }
}
