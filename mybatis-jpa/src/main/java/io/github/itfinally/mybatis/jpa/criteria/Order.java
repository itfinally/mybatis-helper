package io.github.itfinally.mybatis.jpa.criteria;

public interface Order {
  Order reverse();

  boolean isAscending();
}
