package top.itfinally.mybatis.jpa.mapper;

import org.apache.ibatis.annotations.*;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;

import java.util.List;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/23       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public interface BasicCrudMapper<Entity> {
    String ID = "id";
    String IDS = "ids";
    String ENTITY = "entity";
    String ENTITIES = "entities";

    @Select( "" )
    Entity queryByIdIs( @Param( ID ) String id );

    @Select( "" )
    List<Entity> queryByIdIn( @Param( IDS ) List<String> ids );

    @Select( "" )
    List<Entity> queryAll();

    @Select( "" )
    boolean existByIdIs( @Param( ID ) String id );

    @Insert( "" )
    int save( @Param( ENTITY ) Entity entity );

    @Insert( "" )
    int saveWithNonnull( @Param( ENTITY ) Entity entity );

    @Insert( "" )
    int saveAll( @Param( ENTITIES ) List<Entity> entities );

    @Insert( "" )
    int updateByIdIs( @Param( ENTITY ) Entity entity );

    @Update( "" )
    int updateWithNonnullByIdIs( @Param( ENTITY ) Entity entity );

    @Delete( "" )
    int deleteByIdIs( @Param( ID ) String id );

    @Delete( "" )
    int deleteAllByIdIn( @Param( IDS ) List<String> ids );

    CriteriaBuilder getCriteriaBuilder();
}
