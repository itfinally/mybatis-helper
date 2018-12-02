package io.github.itfinally.mybatis.generator.core.database.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface MysqlMetadataMapper extends MetadataMapper {

  @Override
  @Select( "select * from information_schema.tables where table_schema = ( select database() )" )
  List<Map<String, String>> getTables();

  @Override
  @Select( "select * from information_schema.columns where table_schema = ( select database() ) and table_name = #{tableName}" )
  List<Map<String, String>> getColumns( String tableName );

  @Override
  @Select( "select table_info.constraint_name, table_info.constraint_type, column_info.columns, " +
      "column_info.referenced_table_schema, column_info.referenced_table_name, column_info.referenced_column_name " +

      "from ( select constraint_schema, table_name, constraint_name, group_concat( column_name ) as columns, " +
      "referenced_table_schema, referenced_table_name, referenced_column_name " +
      "from information_schema.key_column_usage " +
      "where constraint_schema = ( select database() ) and table_name = #{tableName} " +
      "group by constraint_schema, constraint_name, referenced_table_schema, referenced_table_name, referenced_column_name ) column_info " +

      "join " +

      "( select constraint_schema, table_name, constraint_name, constraint_type from information_schema.table_constraints " +
      "where constraint_schema = ( select database() ) and table_name = #{tableName} ) table_info " +

      "on column_info.constraint_schema = table_info.constraint_schema " +
      "and column_info.table_name = table_info.table_name " +
      "and column_info.constraint_name = table_info.constraint_name " )
  List<Map<String, String>> getTableKeys( @Param( "tableName" ) String tableName );
}
