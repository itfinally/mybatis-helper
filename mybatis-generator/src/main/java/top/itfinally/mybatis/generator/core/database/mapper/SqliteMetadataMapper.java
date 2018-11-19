package top.itfinally.mybatis.generator.core.database.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * All rights reserved.
 * Date: 2018/11/12
 * Author: itfinally
 */
@Mapper
public interface SqliteMetadataMapper extends MetadataMapper {
    @Override
    @Select( "select name, sql from main.sqlite_master where type = 'table'" )
    List<Map<String, String>> getTables();
}
