package io.github.itfinally.mybatis.jpa.criteria.predicate;

import io.github.itfinally.mybatis.jpa.collectors.AbstractCollector;
import io.github.itfinally.mybatis.jpa.criteria.Predicate;
import io.github.itfinally.mybatis.jpa.criteria.path.ExpressionImpl;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractPredicateImpl extends ExpressionImpl<Boolean, AbstractCollector> implements Predicate, Writable {

  private final AtomicBoolean reverse = new AtomicBoolean( false );

  protected AbstractPredicateImpl( CriteriaBuilder builder ) {
    super( builder, null );
  }

  @Override
  protected AbstractCollector queryCollector() {
    throw new UnsupportedOperationException( "Do not calling collector in simple expression." );
  }

  @Override
  public boolean isNegated() {
    return reverse.get();
  }

  @Override
  public Predicate not() {
    boolean val = reverse.get();
    reverse.compareAndSet( val, !val );

    return this;
  }
}
