package top.itfinally.mybatis.jpa.mapper;

import javax.persistence.Table;
import java.util.Map;
import java.util.concurrent.*;

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
public class BasicCrudContextHolder {
    private static ThreadLocal<Class<?>> localClassHolder = new ThreadLocal<>();
    private static ConcurrentMap<Class<?>, FutureTask<Context>> contextMap = new ConcurrentHashMap<>();

    private BasicCrudContextHolder() {
    }

    public static void setEntityClass( Class<?> clazz ) {
        localClassHolder.set( clazz );
    }

    public static Context getContext() {
        final Class<?> clazz = localClassHolder.get();

        if ( null == clazz ) {
            return null;
        }

        if ( !contextMap.containsKey( clazz ) ) {
            contextMap.putIfAbsent( clazz, new FutureTask<>( new Callable<Context>() {
                @Override
                public Context call() throws Exception {
                    Table table = clazz.getAnnotation( Table.class );
                    if ( null == table ) {
                        throw new IllegalStateException( String.format( "Entity '%s' does not specify a table name " +
                                "( maybe missing @Table ? )", clazz.getSimpleName() ) );
                    }

                    return new Context().setTableName( clazz.getAnnotation( Table.class ).name() );
                }
            } ) );
        }

        try {
            return contextMap.get( clazz ).get();

        } catch ( InterruptedException | ExecutionException e ) {
            return null;
        }
    }

    public static void remove() {
        localClassHolder.remove();
    }

    public static class Context {
        private Class<?> entity;
        private String tableName;
        private Map<String, String> namingMapping;
        private Map<String, Class<?>> typeMapping;

        private Context() {
        }

        public Class<?> getEntity() {
            return entity;
        }

        public Context setEntity( Class<?> entity ) {
            this.entity = entity;
            return this;
        }

        public String getTableName() {
            return tableName;
        }

        public Context setTableName( String tableName ) {
            this.tableName = tableName;
            return this;
        }

        public Map<String, String> getNamingMapping() {
            return namingMapping;
        }

        public Context setNamingMapping( Map<String, String> namingMapping ) {
            this.namingMapping = namingMapping;
            return this;
        }

        public Map<String, Class<?>> getTypeMapping() {
            return typeMapping;
        }

        public Context setTypeMapping( Map<String, Class<?>> typeMapping ) {
            this.typeMapping = typeMapping;
            return this;
        }
    }
}
