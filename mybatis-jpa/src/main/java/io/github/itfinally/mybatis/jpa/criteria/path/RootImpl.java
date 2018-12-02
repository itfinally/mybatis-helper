package io.github.itfinally.mybatis.jpa.criteria.path;

import com.google.common.base.Strings;
import io.github.itfinally.mybatis.jpa.collectors.AbstractCollector;
import io.github.itfinally.mybatis.jpa.criteria.*;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;
import io.github.itfinally.mybatis.jpa.entity.EntityMetadata;
import io.github.itfinally.mybatis.jpa.entity.PathMetadata;

public class RootImpl<Entity, Collector extends AbstractCollector> extends FromImpl<Entity, Collector> implements Root<Entity>, Writable {

  private EntityMetadata entityMetadata;
  private String namespace;

  public RootImpl( CriteriaBuilder builder, Collector queryCollector, EntityMetadata entityMetadata ) {
    super( builder, queryCollector );

    this.entityMetadata = entityMetadata;
  }

  public String getNamespace() {
    return namespace;
  }

  @Override
  public Root<Entity> namespace( String namespace ) {
    this.namespace = namespace;
    return this;
  }

  @Override
  public PathMetadata getModel() {
    return new PathMetadata( entityMetadata, null );
  }

  @Override
  public String toFormatString( ParameterBus parameters ) {
    if ( !Strings.isNullOrEmpty( getAlias() ) ) {
      return getAlias();
    }

    if ( !Strings.isNullOrEmpty( namespace ) ) {
      return String.format( "%s_%s", namespace, entityMetadata.getTableName() );
    }

    return entityMetadata.getTableName();
  }
}
