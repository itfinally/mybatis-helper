package top.itfinally.mybatis.jpa.criteria.query;

import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;
import top.itfinally.mybatis.jpa.mapper.BasicCriteriaQueryInterface;
import top.itfinally.mybatis.jpa.utils.TypeMatcher;

import javax.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static top.itfinally.mybatis.jpa.mapper.BasicCriteriaQueryInterface.ENTITY_CLASS;
import static top.itfinally.mybatis.jpa.mapper.BasicCriteriaQueryInterface.SQL;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/9       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class CriteriaQueryManager<Entity> {
    private static final CriteriaBuilder criteriaBuilder = new CriteriaBuilderImpl();
    private final BasicCriteriaQueryInterface criteriaQueryInterface;
    private final Class<Entity> entityClass;

    public CriteriaQueryManager( BasicCriteriaQueryInterface criteriaQueryInterface, Class<Entity> entityClass ) {
        this.criteriaQueryInterface = criteriaQueryInterface;
        this.entityClass = checkEntityClass( entityClass );
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    @SuppressWarnings( "unchecked" )
    public DelegatedTypedQuery<Entity> createQuery( final CriteriaQuery<?> criteriaQuery ) {
        return new DelegatedTypedQuery<Entity>() {

            private Class<?> entityClass = null;

            @Override
            @SuppressWarnings( "unchecked" )
            public <O> DelegatedTypedQuery<O> as( Class<O> entityClass ) {
                this.entityClass = checkEntityClass( entityClass );
                return ( DelegatedTypedQuery<O> ) this;
            }

            @Override
            public List<Entity> getResultList() {
                return criteriaQueryInterface.queryByCondition( getParameters( ( Writable ) criteriaQuery, getEntityClass() ) );
            }

            @Override
            public Entity getSingleResult() {
                List<Entity> results = getResultList();
                return results.isEmpty() ? null : results.get( 0 );
            }

            @Override
            public int executeUpdate() {
                return getResultList().size();
            }

            private Class<?> getEntityClass() {
                return entityClass != null ? entityClass : CriteriaQueryManager.this.entityClass;
            }
        };
    }

    public DelegatedQuery createQuery( final CriteriaUpdate<?> criteriaUpdate ) {
        return new DelegatedQuery() {
            @Override
            public int executeUpdate() {
                return criteriaQueryInterface.updateByCondition( getParameters( ( Writable ) criteriaUpdate, entityClass ) );
            }
        };
    }

    public DelegatedQuery createQuery( final CriteriaDelete<?> criteriaDelete ) {
        return new DelegatedQuery() {
            @Override
            public int executeUpdate() {
                return criteriaQueryInterface.deleteByCondition( getParameters( ( Writable ) criteriaDelete, entityClass ) );
            }
        };
    }

    private <T> Class<T> checkEntityClass( Class<T> entityClass ) {
        Objects.requireNonNull( entityClass, "Entity class require not null" );

        if ( !( TypeMatcher.isBasicType( entityClass )
                || Map.class.isAssignableFrom( entityClass )
                || entityClass.getAnnotation( Table.class ) != null ) ) {

            throw new IllegalArgumentException( "The entity class must be an entity marked with '@Table' or sub class of class Map" );
        }

        return entityClass;
    }

    private Map<String, Object> getParameters( Writable query, Class<?> entityClass ) {
        ParameterBus parameters = new ParameterBus();

        parameters.put( SQL, query.toFormatString( parameters ) );
        parameters.put( ENTITY_CLASS, entityClass );

        return parameters;
    }
}
