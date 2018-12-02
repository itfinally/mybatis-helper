package io.github.itfinally.mybatis.jpa.entity;

import io.github.itfinally.mybatis.jpa.criteria.JoinType;

import java.util.Objects;

public class JoinMetadata {
  private String className;
  private JoinType type;

  public JoinMetadata( String className, JoinType type ) {
    this.className = className;
    this.type = type;
  }

  public String getClassName() {
    return className;
  }

  public JoinType getType() {
    return type;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof JoinMetadata ) ) return false;
    JoinMetadata joinMetadata = ( JoinMetadata ) o;
    return Objects.equals( getClassName(), joinMetadata.getClassName() ) &&
        getType() == joinMetadata.getType();
  }

  @Override
  public int hashCode() {
    return Objects.hash( getClassName(), getType() );
  }
}
