package io.github.itfinally.mybatis.jpa.criteria.query;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Root;

public interface AbstractQuery<Entity> extends AbstractSubQuery {

  <X> Root<X> from( Class<X> entityClass );

  AbstractQuery<Entity> where( Expression<Boolean>... restriction );
}
