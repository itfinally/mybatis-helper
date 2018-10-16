package top.itfinally.mybatis.jpa.criteria.path;

import com.google.common.base.Strings;
import top.itfinally.mybatis.jpa.collectors.AbstractCollector;
import top.itfinally.mybatis.jpa.criteria.*;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.PathMetadata;

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
public class RootImpl<Entity, Collector extends AbstractCollector> extends FromImpl<Entity, Collector> implements Root<Entity>, Writable {

    private EntityMetadata entityMetadata;

    public RootImpl( CriteriaBuilder builder, Collector queryCollector, EntityMetadata entityMetadata ) {
        super( builder, queryCollector );

        this.entityMetadata = entityMetadata;
    }

    @Override
    public PathMetadata getModel() {
        return new PathMetadata( entityMetadata, null );
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        return Strings.isNullOrEmpty( getAlias() ) ? entityMetadata.getTableName() : getAlias();
    }
}
