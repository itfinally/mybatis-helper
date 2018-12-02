package io.github.itfinally.mybatis.jpa.criteria;

import java.util.Collection;

public interface Expression<T> extends Reference<T> {

  Predicate isNull();

  Predicate isNotNull();

  // Expression or real value
  Predicate in( Expression<?> value );

  // Expression or real values
  Predicate in( Collection<?> values );

}
