package top.itfinally.mybatis.generator.core.database.mapper;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public interface InformationMapper {
    List<Map<String, String>> getTables();

    List<Map<String, String>> getColumns( String tableName );

    List<Map<String, String>> getTableKeys( String tableName );
}
