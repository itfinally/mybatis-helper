package io.github.itfinally.mybatis.jpa.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BasicCriteriaQueryInterface {
  String ENTITY_CLASS = "entityClass";
  String SQL = "sql";

  @Select( "" )
  <T> List<T> queryByCondition( Map<String, Object> parameters );

  @Select( "" )
  <T> T querySingleByCondition( Map<String, Object> parameters );

  @Update( "" )
  int updateByCondition( Map<String, Object> parameters );

  @Delete( "" )
  int deleteByCondition( Map<String, Object> parameters );
}
