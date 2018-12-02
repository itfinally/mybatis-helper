package io.github.itfinally.mybatis.jpa.entity;

public class EntityMetadataToken extends EntityMetadata {
  private String token;

  public String getToken() {
    return token;
  }

  public EntityMetadataToken setToken( String token ) {
    this.token = token;
    return this;
  }
}
