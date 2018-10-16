package top.itfinally.mybatis.jpa.criteria.query;

import com.google.common.collect.Lists;
import top.itfinally.mybatis.jpa.collectors.CriteriaQueryCollector;
import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Order;
import top.itfinally.mybatis.jpa.criteria.Reference;
import top.itfinally.mybatis.jpa.criteria.Root;
import top.itfinally.mybatis.jpa.criteria.adapter.AbstractNodeAdapter;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/1       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class CriteriaQueryImpl<Entity> extends AbstractNodeAdapter<CriteriaQueryCollector> implements CriteriaQuery<Entity>, Writable {

    private CriteriaQueryCollector criteriaQueryCollector;

    public CriteriaQueryImpl( CriteriaBuilder builder ) {
        super( builder, null );

        this.criteriaQueryCollector = new CriteriaQueryCollector( criteriaBuilder(), this );
    }

    @Override
    protected CriteriaQueryCollector queryCollector() {
        return criteriaQueryCollector;
    }

    @Override
    public CriteriaQuery<Entity> select( Reference<?>... path ) {
        queryCollector().addSelection( Lists.newArrayList( path ) );
        return this;
    }

    @Override
    public <X> Root<X> from( Class<X> entityClass ) {
        return queryCollector().from( entityClass );
    }

    @Override
    @SafeVarargs
    public final CriteriaQuery<Entity> where( Expression<Boolean>... restriction ) {
        queryCollector().addCondition( Lists.newArrayList( restriction ) );
        return this;
    }

    @Override
    public CriteriaQuery<Entity> groupBy( Reference<?>... restriction ) {
        queryCollector().addGrouping( Lists.newArrayList( restriction ) );
        return this;
    }

    @Override
    @SafeVarargs
    public final CriteriaQuery<Entity> having( Expression<Boolean>... restriction ) {
        queryCollector().addHaving( Lists.newArrayList( restriction ) );
        return this;
    }

    @Override
    public CriteriaQuery<Entity> orderBy( Order... orders ) {
        queryCollector().addOrder( Lists.newArrayList( orders ) );
        return this;
    }

    @Override
    public <T> SubQuery<T> subQuery() {
        return queryCollector().subQuery();
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        return queryCollector().toFormatString( parameters );
    }
}
