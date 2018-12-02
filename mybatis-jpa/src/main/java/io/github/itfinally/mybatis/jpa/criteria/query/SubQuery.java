package io.github.itfinally.mybatis.jpa.criteria.query;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Order;
import io.github.itfinally.mybatis.jpa.criteria.Reference;

public interface SubQuery<Entity> extends AbstractQuery<Entity>, Expression<Entity> {

  SubQuery<Entity> select( Reference<?>... path );

  @Override
  SubQuery<Entity> where( Expression<Boolean>... restriction );

  SubQuery<Entity> groupBy( Reference<?>... restriction );

  SubQuery<Entity> having( Expression<Boolean>... restriction );

  SubQuery<Entity> orderBy( Order... orders );
}
