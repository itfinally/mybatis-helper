package top.itfinally.mybatis.jpa.override;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class MybatisJpaMapperProxyFactory<Mapper, Entity> extends MapperProxyFactory<Mapper> {
    private final Class<Entity> entityClass;
    private final Class<Mapper> mapperInterface;
    private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();

    public MybatisJpaMapperProxyFactory( Class<Entity> entityClass, Class<Mapper> mapperInterface ) {
        super( mapperInterface );
        this.entityClass = entityClass;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Mapper newInstance( SqlSession sqlSession ) {
        return newInstance( new MybatisJpaMapperProxy<>( sqlSession, mapperInterface, entityClass, methodCache ) );
    }
}
