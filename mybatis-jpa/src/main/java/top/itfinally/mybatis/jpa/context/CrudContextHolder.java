package top.itfinally.mybatis.jpa.context;

import top.itfinally.mybatis.core.ThreadLocalDelegatedFactory;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.context.MetadataFactory;

import java.lang.reflect.Method;

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
public class CrudContextHolder {
    private static ThreadLocal<Context> contextThreadLocal = ThreadLocalDelegatedFactory.newThreadLocal();

    private CrudContextHolder() {
    }

    public static void setContext( Class<?> entityClass, Method method ) {
        contextThreadLocal.set( new Context( MetadataFactory.build( entityClass ), method ) );
    }

    public static Context getContext() {
        return contextThreadLocal.get();
    }

    public static void clear() {
        contextThreadLocal.remove();
    }

    public static class Context {
        private final EntityMetadata metadata;
        private final Method method;

        private Context( EntityMetadata metadata, Method method ) {
            this.metadata = metadata;
            this.method = method;
        }

        public EntityMetadata getMetadata() {
            return metadata;
        }

        public Method getMethod() {
            return method;
        }
    }
}
