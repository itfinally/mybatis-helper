package top.itfinally.mybatis.jpa.criteria.path;

import top.itfinally.mybatis.jpa.criteria.Reference;
import top.itfinally.mybatis.jpa.criteria.adapter.AbstractNodeAdapter;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.query.QueryCollector;

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
public class ReferenceImpl<Entity> extends AbstractNodeAdapter implements Reference<Entity> {

    private String alias;

    public ReferenceImpl( CriteriaBuilder criteriaBuilder, QueryCollector queryCollector ) {
        super( criteriaBuilder, queryCollector );
    }

    @Override
    public Reference<Entity> alias( String alias ) {
        this.alias = alias;
        return this;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
