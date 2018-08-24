package top.itfinally.mybatis.jpa.mapper;

import org.apache.ibatis.annotations.Select;
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

    @Select( "queryByIdIs" )
    Entity queryByIdIs( String id );

    @Select( "queryByIdIn" )
    List<Entity> queryByIdIn( List<String> ids );

    @Select( "queryAll" )
    List<Entity> queryAll();

    @Select( "existByIdIs" )
    boolean existByIdIs( String id );

    void save( Entity entity );

    void saveWithNonnull( Entity entity );

    void saveAll( List<Entity> entities );

    void saveAllWithNonnull( List<Entity> entities );

    void update( Entity entity );

    void updateWithNonnull( Entity entity );

    void updateAll( List<Entity> entities );

    void updateAllWithNonnull( List<Entity> entities );

    void deleteByIdIs( String id );

    void deleteByIdIn( List<String> ids );

    CriteriaBuilder<Entity> getCriteriaBuilder();
}
