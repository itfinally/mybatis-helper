package io.github.itfinally.mybatis.jpa.criteria;

public interface Predicate extends Expression<Boolean> {

  boolean isNegated();

  Predicate not();

}
