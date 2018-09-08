package top.itfinally.mybatis.jpa.override;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.itfinally.mybatis.jpa.criteria.CriteriaBuilder;
import top.itfinally.mybatis.jpa.mapper.BasicConditionalMapper;
import top.itfinally.mybatis.jpa.mapper.BasicCrudMapper;
import top.itfinally.mybatis.jpa.context.CrudContextHolder;
import top.itfinally.mybatis.jpa.context.ResultMapContextHolder;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class MybatisJpaMapperProxy<Mapper, Entity> extends MapperProxy<Mapper> {
    private volatile BasicConditionalMapper conditionalMapper;
    private final Configuration configuration;
    private final Class<Entity> entityClass;
    private final Set<String> methodNames;

    public MybatisJpaMapperProxy( SqlSession sqlSession, Class<Mapper> mapperInterface, Class<Entity> entityClass, Map<Method, MapperMethod> methodCache ) {
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
        if ( proxy instanceof BasicCrudMapper ) {

            boolean isCriteriaQuery = method.getName().equals( "getCriteriaBuilder" );
            if ( methodNames.contains( method.getName() ) || isCriteriaQuery ) {
                CrudContextHolder.setContext( entityClass, method );
                ResultMapContextHolder.resultMapInitializing( configuration, CrudContextHolder.getContext() );

                if ( isCriteriaQuery ) {
                    conditionMapperInitializing();
                    return null;
                }
            }
        }

        if ( BasicConditionalMapper.class.isAssignableFrom( proxy.getClass() ) ) {
            ;
        }

        return super.invoke( proxy, method, args );
    }

    private void conditionMapperInitializing() {
        if ( null == conditionalMapper ) {
            synchronized ( this ) {
                if ( null == conditionalMapper ) {
                    conditionalMapper = BasicConditionalMapperInjector.conditionalMapper;
                    BasicConditionalMapperInjector.conditionalMapper = null;
                }
            }
        }
    }

    @Component
    public static class BasicConditionalMapperInjector {
        private static BasicConditionalMapper conditionalMapper;

        @Autowired
        public void setConditionalMapper( BasicConditionalMapper conditionalMapper ) {
            BasicConditionalMapperInjector.conditionalMapper = conditionalMapper;
        }
    }
}
