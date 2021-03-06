package top.itfinally.mybatis.generator.core;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

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
    public static Map<JdbcType, Class<?>> jdbcMapping;

    private TypeMappings() {
    }

    static {
        TypeHandlerRegistry handlerRegistry = new TypeHandlerRegistry();

        try {
            Class<?> javaType;
            TypeHandler<?> handler;

            Map<JdbcType, Class<?>> jdbcMapping = new HashMap<>();

            for ( JdbcType jdbcType : JdbcType.values() ) {
                handler = handlerRegistry.getTypeHandler( jdbcType );

                if ( null == handler ) {
                    continue;
                }

                javaType = handler.getClass().getMethod( "getNullableResult", ResultSet.class, int.class ).getReturnType();
                jdbcMapping.put( jdbcType, javaType );
            }

            TypeMappings.jdbcMapping = Collections.unmodifiableMap( jdbcMapping );

        } catch ( NoSuchMethodException e ) {
            throw new RuntimeException( "Failure to getting type mapping", e );
        }
    }
}
