package top.itfinally.mybatis.jpa.criteria.query;

import com.google.common.collect.Lists;
import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Reference;
import top.itfinally.mybatis.jpa.criteria.Root;
import top.itfinally.mybatis.jpa.criteria.path.ExpressionImpl;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

import javax.persistence.criteria.Order;
import java.util.List;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/2       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class CriteriaSubQueryImpl<Entity> extends ExpressionImpl<Entity> implements SubQuery<Entity>, Writable {

    private final AbstractQuery<?> parent;
    private final QueryCollector queryCollector;

    public CriteriaSubQueryImpl( CriteriaBuilder builder, AbstractQuery<?> parentQuery ) {
        super( builder, null );

        this.parent = parentQuery;
        this.queryCollector = new QueryCollector( criteriaBuilder(), parentQuery, this );
    }

    @Override
    protected QueryCollector queryCollector() {
        return queryCollector;
    }

    @Override
    public SubQuery<Entity> select( Reference<?>... path ) {
        queryCollector().addSelection( Lists.newArrayList( path ) );
        return this;
    }

    @Override
    public <X> Root<X> from( Class<X> entityClass ) {
        return queryCollector().from( entityClass );
    }

    @Override
    public SubQuery<Entity> where( Expression<Boolean>... restriction ) {
        queryCollector().addCondition( Lists.newArrayList( restriction ) );
        return this;
    }

    @Override
    public SubQuery<Entity> groupBy( Reference<?> restriction ) {
        queryCollector().addGrouping( Lists.<Reference<?>>newArrayList( restriction ) );
        return this;
    }

    @Override
    public SubQuery<Entity> having( Expression<Boolean> restriction ) {
        return this;
    }

    @Override
    public SubQuery<Entity> orderBy( Order order ) {
        return null;
    }

    @Override
    public SubQuery<Entity> orderBy( List<Order> orders ) {
        return null;
    }

    @Override
    public <T> SubQuery<T> subQuery() {
        return queryCollector().subQuery();
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        return queryCollector.toFormatString( parameters );
    }
}
