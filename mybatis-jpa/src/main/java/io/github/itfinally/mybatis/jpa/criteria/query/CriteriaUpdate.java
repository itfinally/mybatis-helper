package io.github.itfinally.mybatis.jpa.criteria.query;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Path;

public interface CriteriaUpdate<Entity> extends AbstractQuery<Entity>, AbstractSubQuery {

  CriteriaUpdate<Entity> set( Path<Entity> path, Expression<?> value );

  CriteriaUpdate<Entity> set( Path<Entity> path, Object value );

  @Override
  CriteriaUpdate<Entity> where( Expression<Boolean>... restriction );
}
