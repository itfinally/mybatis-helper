package io.github.itfinally.mybatis.generator.core.database.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OracleMetadataMapper extends MetadataMapper {
  @Override
  @Select( "select table_name from user_tables" )
  List<Map<String, String>> getTables();
}
