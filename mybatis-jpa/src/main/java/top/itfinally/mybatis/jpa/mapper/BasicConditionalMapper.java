package top.itfinally.mybatis.jpa.mapper;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

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
public interface BasicConditionalMapper {
    String NAMESPACE = "top.itfinally.mybatis.jpa.mapper";
    String PARAMETERS = "parameters";

    @Select( "" )
    Object queryByCondition( @Param( PARAMETERS ) Map<String, Object> parameters );

    @Update( "" )
    int updateByCondition( @Param( PARAMETERS ) Map<String, Object> parameters );

    @Delete( "" )
    int deleteByCondition( @Param( PARAMETERS ) Map<String, Object> parameters );
}
