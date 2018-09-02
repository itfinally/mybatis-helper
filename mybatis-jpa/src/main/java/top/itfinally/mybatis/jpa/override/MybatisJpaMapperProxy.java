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
import top.itfinally.mybatis.jpa.mapper.CrudContextHolder;

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
        this.methodNames = Collections.unmodifiableSet( methodNames );
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        if ( proxy instanceof BasicCrudMapper ) {
            if ( methodNames.contains( method.getName() ) ) {
                CrudContextHolder.setContext( entityClass, method );

            } else if ( method.getName().equals( "getCriteriaBuilder" ) ) {
                if ( null == conditionalMapper ) {
                    initialize();
                }

                return null;
            }
        }

        return super.invoke( proxy, method, args );
    }

    private void initialize() {
        synchronized ( this ) {
            if ( null == conditionalMapper ) {
                synchronized ( this ) {
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
