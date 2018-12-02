package io.github.itfinally.mybatis.jpa.criteria.query;

import com.google.common.collect.Lists;
import io.github.itfinally.mybatis.jpa.collectors.CriteriaUpdateCollector;
import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Path;
import io.github.itfinally.mybatis.jpa.criteria.Root;
import io.github.itfinally.mybatis.jpa.criteria.adapter.AbstractNodeAdapter;
import io.github.itfinally.mybatis.jpa.criteria.predicate.ValueWrapper;

public class CriteriaUpdateImpl<Entity> extends AbstractNodeAdapter<CriteriaUpdateCollector> implements CriteriaUpdate<Entity> {

  private final CriteriaUpdateCollector updateCollector;

  public CriteriaUpdateImpl( CriteriaBuilder builder ) {
    super( builder, null );

    this.updateCollector = new CriteriaUpdateCollector( builder, this );
  }

  @Override
  protected CriteriaUpdateCollector queryCollector() {
    return updateCollector;
  }

  @Override
  public CriteriaUpdate<Entity> set( Path<Entity> path, Expression<?> value ) {
    queryCollector().addSetter( path, new ValueWrapper( criteriaBuilder(), value ) );
    return this;
  }

  @Override
  public CriteriaUpdate<Entity> set( Path<Entity> path, Object value ) {
    queryCollector().addSetter( path, new ValueWrapper( criteriaBuilder(), value ) );
    return this;
  }

  @Override
  public <X> Root<X> from( Class<X> entityClass ) {
    return updateCollector.from( entityClass );
  }

  @Override
  @SafeVarargs
  public final CriteriaUpdate<Entity> where( Expression<Boolean>... restriction ) {
    queryCollector().addCondition( Lists.newArrayList( restriction ) );
    return this;
  }

  @Override
  public <T> SubQuery<T> subQuery() {
    return updateCollector.subQuery();
  }
}
