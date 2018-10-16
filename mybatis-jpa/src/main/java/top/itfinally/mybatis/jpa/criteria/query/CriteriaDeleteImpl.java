package top.itfinally.mybatis.jpa.criteria.query;

import com.google.common.collect.Lists;
import top.itfinally.mybatis.jpa.collectors.CriteriaDeleteCollector;
import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Root;
import top.itfinally.mybatis.jpa.criteria.adapter.AbstractNodeAdapter;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/16       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class CriteriaDeleteImpl<Entity> extends AbstractNodeAdapter<CriteriaDeleteCollector> implements CriteriaDelete<Entity> {

    private CriteriaDeleteCollector deleteCollector;

    public CriteriaDeleteImpl( CriteriaBuilder builder ) {
        super( builder, null );

        this.deleteCollector = new CriteriaDeleteCollector( builder, this );
    }

    @Override
    protected CriteriaDeleteCollector queryCollector() {
        return deleteCollector;
    }

    @Override
    public <X> Root<X> from( Class<X> entityClass ) {
        return queryCollector().from( entityClass );
    }

    @Override
    @SafeVarargs
    public final CriteriaDelete<Entity> where( Expression<Boolean>... restriction ) {
        queryCollector().addCondition( Lists.newArrayList( restriction ) );
        return this;
    }

    @Override
    public CriteriaDelete<Entity> delete( Root<?>... roots ) {
        queryCollector().addDeleteRoot( Lists.newArrayList( roots ) );
        return this;
    }

    @Override
    public <T> SubQuery<T> subQuery() {
        return queryCollector().subQuery();
    }
}
