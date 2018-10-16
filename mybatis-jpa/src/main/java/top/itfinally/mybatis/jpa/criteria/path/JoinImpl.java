package top.itfinally.mybatis.jpa.criteria.path;

import com.google.common.collect.Lists;
import top.itfinally.mybatis.jpa.collectors.AbstractCollector;
import top.itfinally.mybatis.jpa.criteria.*;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.entity.PathMetadata;

import javax.persistence.criteria.JoinType;
import java.util.Collection;
import java.util.List;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class JoinImpl<Entity, Collector extends AbstractCollector> extends FromImpl<Entity, Collector> implements Join<Entity> {

    private FromImpl<Entity, Collector> source;

    public JoinImpl( CriteriaBuilder builder, Collector queryCollector, From<Entity> source ) {
        super( builder, queryCollector );

        this.source = ( FromImpl<Entity, Collector> ) source;
    }

    @Override
    public Join<Entity> on( Predicate restrictions ) {
        queryCollector().addJoinCondition( Lists.newArrayList( restrictions ) );
        return this;
    }

    @Override
    public Join<Entity> on( List<Predicate> restrictions ) {
        queryCollector().addJoinCondition( restrictions );
        return this;
    }

    @Override
    public Join<Entity> join( String attributeName ) {
        return source.join( attributeName );
    }

    @Override
    public Join<Entity> join( String attributeName, JoinType jt ) {
        return source.join( attributeName, jt );
    }

    @Override
    public PathMetadata getModel() {
        return source.getModel();
    }

    @Override
    public Path<Entity> get( String attributeName ) {
        return source.get( attributeName );
    }

    @Override
    public Predicate isNull() {
        return source.isNull();
    }

    @Override
    public Predicate isNotNull() {
        return source.isNotNull();
    }

    @Override
    public Predicate in( Expression<?> value ) {
        return source.in( value );
    }

    @Override
    public Predicate in( Collection<?> values ) {
        return source.in( values );
    }

    @Override
    public Reference<Entity> alias( String alias ) {
        return source.alias( alias );
    }

    @Override
    public String getAlias() {
        return source.getAlias();
    }
}
