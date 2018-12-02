package io.github.itfinally.mybatis.jpa.criteria;

public interface Reference<T> {
  Reference<T> alias( String alias );

  String getAlias();
}
