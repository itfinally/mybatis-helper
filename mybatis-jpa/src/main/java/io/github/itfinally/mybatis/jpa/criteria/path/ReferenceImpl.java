package io.github.itfinally.mybatis.jpa.criteria.path;

import io.github.itfinally.mybatis.jpa.collectors.AbstractCollector;
import io.github.itfinally.mybatis.jpa.criteria.Reference;
import io.github.itfinally.mybatis.jpa.criteria.adapter.AbstractNodeAdapter;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;

public class ReferenceImpl<Value, Collector extends AbstractCollector> extends AbstractNodeAdapter<Collector>
    implements Reference<Value> {

  private String alias;

  public ReferenceImpl( CriteriaBuilder criteriaBuilder, Collector queryCollector ) {
    super( criteriaBuilder, queryCollector );
  }

  @Override
  public Reference<Value> alias( String alias ) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAlias() {
    return alias;
  }
}
