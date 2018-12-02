package io.github.itfinally.mybatis.jpa.mapper;

import org.apache.ibatis.annotations.*;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaQueryManager;

import java.util.List;

public interface BasicCrudMapper<Entity> {
  String ID = "id";
  String IDS = "ids";
  String ENTITY = "entity";
  String ENTITIES = "entities";

  @Select( "" )
  Entity queryByIdIs( @Param( ID ) String id );

  @Select( "" )
  List<Entity> queryByIdIn( @Param( IDS ) List<String> ids );

  @Select( "" )
  List<Entity> queryAll();

  @Select( "" )
  boolean existByIdIs( @Param( ID ) String id );

  @Insert( "" )
  int save( @Param( ENTITY ) Entity entity );

  @Insert( "" )
  int saveWithNonnull( @Param( ENTITY ) Entity entity );

  @Insert( "" )
  int saveAll( @Param( ENTITIES ) List<Entity> entities );

  @Insert( "" )
  int updateByIdIs( @Param( ENTITY ) Entity entity );

  @Update( "" )
  int updateWithNonnullByIdIs( @Param( ENTITY ) Entity entity );

  @Delete( "" )
  int deleteByIdIs( @Param( ID ) String id );

  @Delete( "" )
  int deleteAllByIdIn( @Param( IDS ) List<String> ids );

  CriteriaQueryManager<Entity> getCriteriaQueryManager();
}
