package io.github.itfinally.mybatis.jpa.criteria;

public interface Root<Entity> extends From<Entity> {
  Root<Entity> namespace( String namespace );
}
