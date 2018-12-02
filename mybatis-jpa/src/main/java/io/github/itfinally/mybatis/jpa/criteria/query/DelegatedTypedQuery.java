package io.github.itfinally.mybatis.jpa.criteria.query;

import java.util.List;

public interface DelegatedTypedQuery<Entity> extends DelegatedQuery {

  <O> DelegatedTypedQuery<O> as( Class<O> entityClass );

  List<Entity> getResultList();

  Entity getSingleResult();
}
