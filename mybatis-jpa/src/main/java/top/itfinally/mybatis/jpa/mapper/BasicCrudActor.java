package top.itfinally.mybatis.jpa.mapper;

import com.google.common.reflect.TypeToken;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import top.itfinally.mybatis.jpa.criteria.CriteriaBuilder;

import java.util.List;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/24       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Repository
@SuppressWarnings( "unchecked" )
public class BasicCrudActor<Entity> extends SqlSessionDaoSupport implements BasicCrudMapper<Entity> {
    private final Class<Entity> entityClass;

    public BasicCrudActor( SqlSessionFactory sqlSessionFactory ) {
        TypeToken<Entity> typeToken = new TypeToken<Entity>( getClass() ){};
        entityClass = ( Class<Entity> ) typeToken.getRawType();

        setSqlSessionFactory( sqlSessionFactory );
        sqlSessionFactory.getConfiguration().addMapper( BasicCrudMapper.class );
    }

    @Override
    public Entity queryByIdIs( String id ) {
        return ( Entity ) getSqlSession().getMapper( BasicCrudMapper.class ).queryByIdIs( id );
    }

    @Override
    public List<Entity> queryByIdIn( List<String> ids ) {
        return null;
    }

    @Override
    public List<Entity> queryAll() {
        return null;
    }

    @Override
    public boolean existByIdIs( String id ) {
        return false;
    }

    @Override
    public void save( Entity entity ) {

    }

    @Override
    public void saveWithNonnull( Entity entity ) {

    }

    @Override
    public void saveAll( List<Entity> entities ) {

    }

    @Override
    public void saveAllWithNonnull( List<Entity> entities ) {

    }

    @Override
    public void update( Entity entity ) {

    }

    @Override
    public void updateWithNonnull( Entity entity ) {

    }

    @Override
    public void updateAll( List<Entity> entities ) {

    }

    @Override
    public void updateAllWithNonnull( List<Entity> entities ) {

    }

    @Override
    public void deleteByIdIs( String id ) {

    }

    @Override
    public void deleteByIdIn( List<String> ids ) {

    }

    @Override
    public CriteriaBuilder<Entity> getCriteriaBuilder() {
        return null;
    }
}
