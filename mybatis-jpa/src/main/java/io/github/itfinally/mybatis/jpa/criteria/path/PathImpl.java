package io.github.itfinally.mybatis.jpa.criteria.path;

import io.github.itfinally.mybatis.jpa.collectors.AbstractCollector;
import io.github.itfinally.mybatis.jpa.criteria.Path;
import io.github.itfinally.mybatis.jpa.criteria.Root;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.entity.AttributeMetadata;
import io.github.itfinally.mybatis.jpa.entity.EntityMetadata;
import io.github.itfinally.mybatis.jpa.entity.PathMetadata;
import io.github.itfinally.mybatis.jpa.entity.ForeignAttributeMetadata;
import io.github.itfinally.mybatis.jpa.exception.NoSuchAttributeException;

public class PathImpl<Entity, Collector extends AbstractCollector> extends ExpressionImpl<Entity, Collector> implements Path<Entity> {

  public PathImpl( CriteriaBuilder criteriaBuilder, Collector queryCollector ) {
    super( criteriaBuilder, queryCollector );
  }

  @Override
  @SuppressWarnings( "unchecked" )
  public Path<Entity> get( String attributeName ) {
    AttributeMetadata attributeMetadata = getAttribute( getRealType( Root.class, this )
        .getModel().getEntityMetadata(), attributeName );

    return new AttributePath<>( criteriaBuilder(), queryCollector(), ( Root<?> ) this, attributeMetadata );
  }

  protected static AttributeMetadata getAttribute( EntityMetadata entityMetadata, String attributeName ) {
    for ( AttributeMetadata item : entityMetadata.getColumns() ) {
      if ( item.getJavaName().equals( attributeName ) ) {
        return item;
      }
    }

    for ( ForeignAttributeMetadata item : entityMetadata.getReferenceColumns() ) {
      if ( item.getJavaName().equals( attributeName ) ) {
        return item;
      }
    }

    throw new NoSuchAttributeException( String.format( "No such attribute '%s' from entity '%s'",
        attributeName, entityMetadata.getEntityClass().getName() ) );
  }

  @Override
  public PathMetadata getModel() {
    throw new UnsupportedOperationException( "Unsupported get path model before getting specify attribute." );
  }
}
