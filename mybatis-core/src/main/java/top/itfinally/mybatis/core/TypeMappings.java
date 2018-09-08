package top.itfinally.mybatis.core;

import com.google.common.collect.Sets;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import javax.persistence.Column;
import java.sql.ResultSet;
import java.util.*;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/4       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class TypeMappings {
    private static Map<Class<?>, Class<?>> wrapperTypes;
    private static Map<JdbcType, Class<?>> jdbcMapping;
    private static Map<Class<?>, JdbcType> javaMapping;

    private TypeMappings() {
    }

    static {
        TypeHandlerRegistry handlerRegistry = new TypeHandlerRegistry();

        typePatches();

        try {
            Class<?> javaType;
            TypeHandler<?> handler;

            Map<JdbcType, Class<?>> jdbcMapping = new HashMap<>();
            Map<Class<?>, JdbcType> javaMapping = new HashMap<>();

            for ( JdbcType jdbcType : JdbcType.values() ) {
                handler = handlerRegistry.getTypeHandler( jdbcType );

                if ( null == handler ) {
                    continue;
                }

                javaType = handler.getClass().getMethod( "getNullableResult", ResultSet.class, int.class ).getReturnType();
                jdbcMapping.put( jdbcType, javaType );
                javaMapping.put( javaType, jdbcType );

                if ( wrapperTypes.containsKey( javaType ) ) {
                    javaMapping.put( wrapperTypes.get( javaType ), jdbcType );
                }
            }

            TypeMappings.jdbcMapping = Collections.unmodifiableMap( jdbcMapping );
            TypeMappings.javaMapping = Collections.unmodifiableMap( javaMapping );

        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException( "Failure to getting type mapping", e );
        }
    }

    private static void typePatches() {
        Map<Class<?>, Class<?>> wrapperTypes = new HashMap<>();
        wrapperTypes.put( Boolean.class, boolean.class );
        wrapperTypes.put( Byte.class, byte.class );
        wrapperTypes.put( Character.class, byte.class );
        wrapperTypes.put( Short.class, short.class );
        wrapperTypes.put( Integer.class, int.class );
        wrapperTypes.put( Long.class, long.class );
        wrapperTypes.put( Float.class, float.class );
        wrapperTypes.put( Double.class, double.class );

        TypeMappings.wrapperTypes = Collections.unmodifiableMap( wrapperTypes );
    }

    public static Map<JdbcType, Class<?>> getJdbcMapping() {
        return jdbcMapping;
    }

    public static Map<Class<?>, JdbcType> getJavaMapping() {
        return javaMapping;
    }

    private static class TypeEntry {
        private final Class<?> javaType;
        private final JdbcType jdbcType;

        public TypeEntry( Class<?> javaType, JdbcType jdbcType ) {
            this.javaType = javaType;
            this.jdbcType = jdbcType;
        }

        public Class<?> getJavaType() {
            return javaType;
        }

        public JdbcType getJdbcType() {
            return jdbcType;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( !( o instanceof TypeEntry ) ) return false;
            TypeEntry typeEntry = ( TypeEntry ) o;
            return Objects.equals( getJavaType(), typeEntry.getJavaType() ) &&
                    getJdbcType() == typeEntry.getJdbcType();
        }

        @Override
        public int hashCode() {
            return Objects.hash( getJavaType(), getJdbcType() );
        }

        @Override
        public String toString() {
            return "TypeEntry{" +
                    "javaType=" + javaType +
                    ", jdbcType=" + jdbcType +
                    '}';
        }
    }
}
