package io.github.itfinally.mybatis.generator.core.database.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SqliteMetadataMapper extends MetadataMapper {
  @Override
  @Select( "select name, sql from main.sqlite_master where type = 'table'" )
  List<Map<String, String>> getTables();
}
