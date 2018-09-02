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
 * 通过使用 Map 接收各种参数, 并且交由 mybatis 做第一次的参数解析
 * 借用 mybatis 本身的能力来减轻开发工作
 *
 * </pre>
 */
@Mapper
@Component
public interface BasicConditionalMapper {
    String PARAMETERS = "parameters";

    @Select( "" )
    Object queryByCondition( @Param( PARAMETERS ) Map<String, Object> parameters );

    @Update( "" )
    int updateByCondition( @Param( PARAMETERS ) Map<String, Object> parameters );

    @Delete( "" )
    int deleteByCondition( @Param( PARAMETERS ) Map<String, Object> parameters );
}
