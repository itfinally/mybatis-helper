package top.itfinally.mybatis.jpa.mapper;

import org.apache.ibatis.annotations.*;
import top.itfinally.mybatis.jpa.criteria.CriteriaBuilder;

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
    void save( @Param( ENTITY ) Entity entity );

    @Insert( "" )
    void saveWithNonnull( @Param( ENTITY ) Entity entity );

    @Insert( "" )
    void saveAll( @Param( ENTITIES ) List<Entity> entities );

    @Insert( "" )
    void updateByIdIs( @Param( ENTITY ) Entity entity );

    @Update( "" )
    void updateWithNonnullByIdIs( @Param( ENTITY ) Entity entity );

    @Delete( "" )
    void deleteByIdIs( @Param( ID ) String id );

    @Delete( "" )
    void deleteAllByIdIn( @Param( IDS ) List<String> ids );

    CriteriaBuilder<Entity> getCriteriaBuilder();
}
