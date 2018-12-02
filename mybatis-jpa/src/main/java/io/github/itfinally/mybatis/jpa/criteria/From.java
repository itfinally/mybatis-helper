package io.github.itfinally.mybatis.jpa.criteria;

public interface From<Entity> extends Path<Entity> {

  Join<Entity> join( String attributeName );

  Join<Entity> join( String attributeName, JoinType jt );
}
