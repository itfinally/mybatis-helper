package io.github.itfinally.mybatis.jpa.entity;

public class PathMetadata {
  private final EntityMetadata entityMetadata;
  private final AttributeMetadata attributeMetadata;

  public PathMetadata( EntityMetadata entityMetadata, AttributeMetadata attributeMetadata ) {
    this.entityMetadata = entityMetadata;
    this.attributeMetadata = attributeMetadata;
  }

  public EntityMetadata getEntityMetadata() {
    return entityMetadata;
  }

  public AttributeMetadata getAttributeMetadata() {
    return attributeMetadata;
  }

  public boolean isForeignKey() {
    return attributeMetadata instanceof ForeignAttributeMetadata;
  }
}
