package top.itfinally.mybatis.jpa.criteria.path;

import top.itfinally.mybatis.jpa.collectors.AbstractCollector;
import top.itfinally.mybatis.jpa.criteria.Reference;
import top.itfinally.mybatis.jpa.criteria.adapter.AbstractNodeAdapter;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/29       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ReferenceImpl<Value, Collector extends AbstractCollector> extends AbstractNodeAdapter<Collector>
        implements Reference<Value> {

    private String alias;

    public ReferenceImpl( CriteriaBuilder criteriaBuilder, Collector queryCollector ) {
        super( criteriaBuilder, queryCollector );
    }

    @Override
    public Reference<Value> alias( String alias ) {
        this.alias = alias;
        return this;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
