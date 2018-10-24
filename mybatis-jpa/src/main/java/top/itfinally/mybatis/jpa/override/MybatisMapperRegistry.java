package top.itfinally.mybatis.jpa.override;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import top.itfinally.mybatis.jpa.exception.NoSuchActualTypeException;
import top.itfinally.mybatis.jpa.mapper.BasicCrudMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
public class MybatisMapperRegistry extends MapperRegistry {
    private static final Cache<Class<?>, MapperProxyFactory<?>> knownMappers = CacheBuilder.newBuilder()
            .concurrencyLevel( Runtime.getRuntime().availableProcessors() )
            .maximumSize( 1024 )
            .initialCapacity( 64 )
            .build();

    public MybatisMapperRegistry( Configuration config ) {
        super( config );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <Mapper> Mapper getMapper( final Class<Mapper> type, SqlSession sqlSession ) {
        if ( !getMappers().contains( type ) ) {
            throw new BindingException( "Type " + type + " is not known to the MapperRegistry." );
        }

        try {
            return ( Mapper ) knownMappers.get( type, new Callable<MapperProxyFactory<?>>() {
                @Override
                public MapperProxyFactory<?> call() {
                    if ( !BasicCrudMapper.class.isAssignableFrom( type ) ) {
                        return new MybatisMapperProxyFactory<>( null, type );
                    }

                    Type[] types = type.getGenericInterfaces();
                    if ( null == types || types.length <= 0 || !( types[ 0 ] instanceof ParameterizedType ) ) {
                        throw new NoSuchActualTypeException( "No actual type found from generic type " +
                                String.format( "( maybe you missing generic type, expect '%s<ActualType>' but got '%s' )",
                                        type.getSimpleName(), type.getSimpleName() ) );
                    }

                    ParameterizedType pt = ( ParameterizedType ) types[ 0 ];
                    Type genericType = pt.getActualTypeArguments()[ 0 ];

                    if ( genericType instanceof ParameterizedType || genericType instanceof TypeVariable ) {
                        throw new IllegalArgumentException( "" );
                    }

                    return new MybatisMapperProxyFactory<>( ( Class<?> ) pt.getActualTypeArguments()[ 0 ], type );

                }

            } ).newInstance( sqlSession );

        } catch ( ExecutionException e ) {
            throw new IllegalStateException( "Failure to initializing mapper", e.getCause() );
        }
    }
}
