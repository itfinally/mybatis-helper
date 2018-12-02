package io.github.itfinally.mybatis.jpa.criteria.query;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Root;

public interface CriteriaDelete<Entity> extends AbstractQuery<Entity> {

  @Override
  CriteriaDelete<Entity> where( Expression<Boolean>... restriction );

  CriteriaDelete<Entity> delete( Root<?>... roots );
}
