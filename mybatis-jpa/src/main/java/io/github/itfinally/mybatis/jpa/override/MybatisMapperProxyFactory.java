package io.github.itfinally.mybatis.jpa.override;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MybatisMapperProxyFactory<Mapper, Entity> extends MapperProxyFactory<Mapper> {
  private final Class<Entity> entityClass;
  private final Class<Mapper> mapperInterface;
  private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();

  public MybatisMapperProxyFactory( Class<Entity> entityClass, Class<Mapper> mapperInterface ) {
    super( mapperInterface );
    this.entityClass = entityClass;
    this.mapperInterface = mapperInterface;
  }

  @Override
  public Mapper newInstance( SqlSession sqlSession ) {
    return newInstance( new MybatisMapperProxy<>( sqlSession, mapperInterface, entityClass, methodCache ) );
  }
}
