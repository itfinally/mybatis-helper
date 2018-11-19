package top.itfinally.mybatis.generator.core.database.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Mapper
public interface OracleMetadataMapper extends MetadataMapper {
    @Override
    @Select( "select table_name from user_tables" )
    List<Map<String, String>> getTables();
}
