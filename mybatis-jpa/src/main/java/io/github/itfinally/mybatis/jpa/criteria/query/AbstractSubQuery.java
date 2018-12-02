package io.github.itfinally.mybatis.jpa.criteria.query;

public interface AbstractSubQuery {
  <T> SubQuery<T> subQuery();
}
