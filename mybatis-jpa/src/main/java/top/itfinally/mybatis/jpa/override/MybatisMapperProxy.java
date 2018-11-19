package top.itfinally.mybatis.jpa.override;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.itfinally.mybatis.jpa.MybatisJpaConfigureProperties;
import top.itfinally.mybatis.jpa.MybatisJpaEntityScanner;
import top.itfinally.mybatis.jpa.context.MetadataFactory;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaQueryManager;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.mapper.BasicCriteriaQueryInterface;
import top.itfinally.mybatis.jpa.mapper.BasicCrudMapper;
import top.itfinally.mybatis.jpa.context.CrudContextHolder;
import top.itfinally.mybatis.jpa.utils.TypeMatcher;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static top.itfinally.mybatis.jpa.context.CrudContextHolder.ContextType.JPA;
import static top.itfinally.mybatis.jpa.mapper.BasicCriteriaQueryInterface.ENTITY_CLASS;

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
public class MybatisMapperProxy<Mapper, Entity> extends MapperProxy<Mapper> {
    private final Configuration configuration;
    private final Class<Entity> entityClass;
    private final Set<String> methodNames;

    private volatile boolean isInstalled = false;

    public MybatisMapperProxy( SqlSession sqlSession, Class<Mapper> mapperInterface, Class<Entity> entityClass, Map<Method, MapperMethod> methodCache ) {
        super( sqlSession, mapperInterface, methodCache );

        Set<String> methodNames = new HashSet<>();
        for ( Method method : BasicCrudMapper.class.getDeclaredMethods() ) {
            if ( !CriteriaBuilder.class.isAssignableFrom( method.getReturnType() ) ) {
                methodNames.add( method.getName() );
            }
        }

        this.entityClass = entityClass;
        this.configuration = sqlSession.getConfiguration();
        this.methodNames = Collections.unmodifiableSet( methodNames );
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        if ( proxy instanceof BasicCrudMapper || proxy instanceof BasicCriteriaQueryInterface ) {
            // Delay initialize entity's metadata
            if ( !isInstalled ) {
                synchronized ( this ) {
                    if ( !isInstalled ) {
                        new MybatisJpaEntityScanner().scan( BasicConditionalMapperInjector.properties );
                        isInstalled = true;
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

        try {
            return super.invoke( proxy, method, args );

        } finally {
            CrudContextHolder.clear();
        }
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
