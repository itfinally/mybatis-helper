package io.github.itfinally.mybatis.jpa.criteria;

import io.github.itfinally.mybatis.jpa.entity.PathMetadata;

public interface Path<Entity> extends Expression<Entity> {

  PathMetadata getModel();

  Path<Entity> get( String attributeName );

}
