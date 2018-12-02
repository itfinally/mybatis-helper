package io.github.itfinally.mybatis.jpa.criteria.query;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Order;
import io.github.itfinally.mybatis.jpa.criteria.Reference;

public interface CriteriaQuery<Entity> extends AbstractQuery<Entity>, AbstractSubQuery {

  CriteriaQuery<Entity> select( Reference<?>... path );

  @Override
  CriteriaQuery<Entity> where( Expression<Boolean>... restrictions );

  CriteriaQuery<Entity> groupBy( Reference<?>... restriction );

  CriteriaQuery<Entity> having( Expression<Boolean>... restriction );

  CriteriaQuery<Entity> orderBy( Order... orders );
}
