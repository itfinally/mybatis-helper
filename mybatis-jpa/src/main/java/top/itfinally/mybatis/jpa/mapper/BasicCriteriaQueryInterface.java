package top.itfinally.mybatis.jpa.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/2       itfinally       首次创建
 * *********************************************
 *
 * </pre>
 */
@Mapper
@Component
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
