package top.itfinally.mybatis.jpa.override;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Map;

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
public class MybatisJpaMapperProxy<Entity> extends MapperProxy<Entity> {
    public MybatisJpaMapperProxy( SqlSession sqlSession, Class<Entity> mapperInterface, Map<Method, MapperMethod> methodCache ) {
        super( sqlSession, mapperInterface, methodCache );
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        return super.invoke( proxy, method, args );
    }
}
