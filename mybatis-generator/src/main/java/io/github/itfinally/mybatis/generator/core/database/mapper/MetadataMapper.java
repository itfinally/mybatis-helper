package io.github.itfinally.mybatis.generator.core.database.mapper;

import java.util.List;
import java.util.Map;

public interface MetadataMapper {
  List<Map<String, String>> getTables();

  List<Map<String, String>> getColumns( String tableName );

  List<Map<String, String>> getTableKeys( String tableName );
}
