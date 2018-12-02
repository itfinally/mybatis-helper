package io.github.itfinally.mybatis.jpa.criteria.adapter;

import io.github.itfinally.mybatis.jpa.collectors.AbstractCollector;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;

import java.util.Objects;

public abstract class AbstractNodeAdapter<Collector extends AbstractCollector> {
  private final CriteriaBuilder criteriaBuilder;
  private final Collector queryCollector;

  public AbstractNodeAdapter( CriteriaBuilder criteriaBuilder, Collector queryCollector ) {
    this.criteriaBuilder = criteriaBuilder;
    this.queryCollector = queryCollector;
  }

  protected CriteriaBuilder criteriaBuilder() {
    return criteriaBuilder;
  }

  protected Collector queryCollector() {
    return queryCollector;
  }

  protected static <T> T getRealType( Class<T> clazz, Object target ) {
    Objects.requireNonNull( clazz, "Class require not null" );
    Objects.requireNonNull( target, "Target instance require not null" );

    if ( !clazz.isAssignableFrom( target.getClass() ) ) {
      throw new IllegalArgumentException( String.format( "Method expect given type %s but got %s",
          clazz.getName(), target.getClass().getName() ) );
    }

    return clazz.cast( target );
  }
}
