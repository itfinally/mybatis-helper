package io.github.itfinally.mybatis.jpa.override;

import io.github.itfinally.logger.CheckedLogger;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.github.itfinally.mybatis.jpa.MybatisJpaConfigureProperties;
import io.github.itfinally.mybatis.jpa.MybatisJpaEntityScanner;
import io.github.itfinally.mybatis.jpa.context.MetadataFactory;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaQueryManager;
import io.github.itfinally.mybatis.jpa.entity.EntityMetadata;
import io.github.itfinally.mybatis.jpa.mapper.BasicCriteriaQueryInterface;
import io.github.itfinally.mybatis.jpa.mapper.BasicCrudMapper;
import io.github.itfinally.mybatis.jpa.context.CrudContextHolder;
import io.github.itfinally.mybatis.jpa.utils.TypeMatcher;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.github.itfinally.mybatis.jpa.context.CrudContextHolder.ContextType.JPA;
import static io.github.itfinally.mybatis.jpa.mapper.BasicCriteriaQueryInterface.ENTITY_CLASS;

public class MybatisMapperProxy<Mapper, Entity> extends MapperProxy<Mapper> {
  private static final CheckedLogger logger = new CheckedLogger( MybatisMapperProxy.class );

  private volatile static boolean isInstalled = false;

  private final Class<Entity> entityClass;
  private final Set<String> methodNames;

  public MybatisMapperProxy( SqlSession sqlSession, Class<Mapper> mapperInterface, Class<Entity> entityClass, Map<Method, MapperMethod> methodCache ) {
    super( sqlSession, mapperInterface, methodCache );

    Set<String> methodNames = new HashSet<>();
    for ( Method method : BasicCrudMapper.class.getDeclaredMethods() ) {
      if ( !CriteriaBuilder.class.isAssignableFrom( method.getReturnType() ) ) {
        methodNames.add( method.getName() );
      }
    }

    this.entityClass = entityClass;
    this.methodNames = Collections.unmodifiableSet( methodNames );
  }

  @Override
  public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
    if ( proxy instanceof BasicCrudMapper || proxy instanceof BasicCriteriaQueryInterface ) {
      // Delay initialize entity's metadata
      if ( !isInstalled ) {
        synchronized ( this ) {
          if ( !isInstalled ) {

            logger.info( "Ready to scan jpa entity and built" );

            MybatisJpaEntityScanner.scan( BasicConditionalMapperInjector.properties );
            isInstalled = true;

            logger.info( "All jpa entity's metadata has completed built" );
          }
        }
      }

      if ( proxy instanceof BasicCrudMapper ) {
        if ( method.getReturnType() == CriteriaQueryManager.class ) {
          return new CriteriaQueryManager<>( BasicConditionalMapperInjector.conditionalMapper, entityClass );
        }

        if ( methodNames.contains( method.getName() ) ) {
          CrudContextHolder.buildEntityAndSetContext( entityClass, method );
        }

      } else {
        Class<?> clazz = ( Class<?> ) ( ( Map ) args[ 0 ] ).get( ENTITY_CLASS );
        EntityMetadata metadata = null;

        if ( !( TypeMatcher.isBasicType( clazz ) || Map.class.isAssignableFrom( clazz ) ) ) {
          metadata = MetadataFactory.getMetadata( clazz );
        }

        CrudContextHolder.setContext( new CrudContextHolder.Context( JPA, metadata, method ) );
      }
    }

    return super.invoke( proxy, method, args );
  }

  @Component
  public static class BasicConditionalMapperInjector {
    private static volatile BasicCriteriaQueryInterface conditionalMapper;
    private static volatile MybatisJpaConfigureProperties properties;

    @Autowired
    public void setConditionalMapper( BasicCriteriaQueryInterface conditionalMapper ) {
      BasicConditionalMapperInjector.conditionalMapper = conditionalMapper;
    }

    @Autowired
    public void setProperties( MybatisJpaConfigureProperties properties ) {
      BasicConditionalMapperInjector.properties = properties;
    }
  }
}
