package top.itfinally.mybatis.jpa.override;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
public class MybatisJpaMapperRegistry extends MapperRegistry {
    private final ConcurrentMap<Class<?>, FutureTask<MapperProxyFactory<?>>> knownMappers = new ConcurrentHashMap<>();

    public MybatisJpaMapperRegistry( Configuration config ) {
        super( config );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <Mapper> Mapper getMapper( Class<Mapper> mapper, SqlSession sqlSession ) {
        if ( !getMappers().contains( mapper ) ) {
            throw new BindingException( "Type " + mapper + " is not known to the MapperRegistry." );
        }

        if ( !knownMappers.containsKey( mapper ) && null == knownMappers.putIfAbsent( mapper, makePromise( mapper ) ) ) {
            knownMappers.get( mapper ).run();
        }

        try {
            return ( Mapper ) knownMappers.get( mapper ).get().newInstance( sqlSession );

        } catch ( InterruptedException | ExecutionException e ) {
            throw new IllegalStateException( "Failure to initializing mapper", e );
        }
    }

    private FutureTask<MapperProxyFactory<?>> makePromise( final Class<?> mapper ) {
        return new FutureTask<>( new Callable<MapperProxyFactory<?>>() {
            @Override
            public MapperProxyFactory<?> call() {
                Type[] types = mapper.getGenericInterfaces();
                if ( null == types || types.length <= 0 || !( types[ 0 ] instanceof ParameterizedType ) ) {
                    return new MybatisJpaMapperProxyFactory<>( null, mapper );
                }

                ParameterizedType pt = ( ParameterizedType ) types[ 0 ];
                Class<?> entityClass = ( Class<?> ) pt.getActualTypeArguments()[ 0 ];

                return new MybatisJpaMapperProxyFactory<>( entityClass, mapper );
            }
        } );
    }
}
