package io.github.itfinally.mybatis.jpa.criteria.predicate;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Order;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.Objects;

public class OrderImpl implements Order, Writable {

  private final Expression<?> expression;
  private boolean ascending;

  public OrderImpl( Expression<?> expression, boolean ascending ) {
    this.expression = Objects.requireNonNull( expression, "Expression require not null" );
    this.ascending = ascending;
  }

  @Override
  public Order reverse() {
    ascending = !ascending;
    return this;
  }

  @Override
  public boolean isAscending() {
    return ascending;
  }

  @Override
  public String toFormatString( ParameterBus parameters ) {
    return String.format( "%s %s", ( ( Writable ) expression ).toFormatString( parameters ), ascending ? "asc" : "desc" );
  }
}
