package io.github.itfinally.mybatis.jpa.criteria.path;

import io.github.itfinally.mybatis.jpa.collectors.AbstractCollector;
import io.github.itfinally.mybatis.jpa.criteria.*;
import io.github.itfinally.mybatis.jpa.entity.JoinMetadata;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.entity.AttributeMetadata;
import io.github.itfinally.mybatis.jpa.entity.ForeignAttributeMetadata;

public class FromImpl<Entity, Collector extends AbstractCollector> extends PathImpl<Entity, Collector> implements From<Entity> {

  public FromImpl( CriteriaBuilder criteriaBuilder, Collector queryCollector ) {
    super( criteriaBuilder, queryCollector );
  }

  @Override
  public Join<Entity> join( String attributeName ) {
    return join( attributeName, JoinType.INNER );
  }

  @Override
  public Join<Entity> join( String attributeName, JoinType jt ) {
    AttributeMetadata attributeMetadata = getRealType( Path.class, this )
        .get( attributeName ).getModel().getAttributeMetadata();

    if ( !( attributeMetadata instanceof ForeignAttributeMetadata ) ) {
      throw new IllegalStateException( String.format( "Attribute '%s' of entity '%s' is not a foreign key ( " +
              "Maybe you missing declared it by something like @OneToOne annotation ? )",
          attributeMetadata.getJavaName(), attributeMetadata.getField().getDeclaringClass().getName() ) );
    }

    queryCollector().addJoiner( new JoinMetadata( getRealType( ForeignAttributeMetadata.class, attributeMetadata )
        .getEntityMetadata().getEntityClass().getName(), jt ) );

    return new JoinImpl<>( criteriaBuilder(), queryCollector(), this );
  }
}
