package io.github.itfinally.mybatis.jpa.criteria;

import java.util.List;

public interface Join<Entity> extends From<Entity> {

  Join<Entity> on( Predicate restrictions );

  Join<Entity> on( List<Predicate> restrictions );

}
