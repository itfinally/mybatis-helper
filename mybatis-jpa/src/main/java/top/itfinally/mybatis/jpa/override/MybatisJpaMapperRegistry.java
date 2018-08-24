package top.itfinally.mybatis.jpa.override;

import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
public class MybatisJpaMapperRegistry extends MapperRegistry {
    private final ConcurrentMap<Class<?>, MapperProxyFactory<?>> knownMappers = new ConcurrentHashMap<>();

    public MybatisJpaMapperRegistry( Configuration config ) {
        super( config );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <Entity> Entity getMapper( Class<Entity> type, SqlSession sqlSession ) {
        Entity mapper = super.getMapper( type, sqlSession );
        if ( null == mapper ) {
            return null;
        }

        if ( !knownMappers.containsKey( type ) ) {
            knownMappers.putIfAbsent( type, new MybatisJpaMapperProxyFactory<>( type ) );
        }

        return ( ( MapperProxyFactory<Entity> ) knownMappers.get( type ) ).newInstance( sqlSession );
    }
}
