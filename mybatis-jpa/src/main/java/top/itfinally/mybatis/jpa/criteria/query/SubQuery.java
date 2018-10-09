package top.itfinally.mybatis.jpa.criteria.query;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Reference;

import javax.persistence.criteria.Order;
import java.util.Collection;
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
public interface SubQuery<Entity> extends AbstractQuery<Entity>, Expression<Entity> {

    SubQuery<Entity> select( Reference<?>... path );

    @Override
    SubQuery<Entity> where( Expression<Boolean>... restriction );

    SubQuery<Entity> groupBy( Reference<?> restriction );

    SubQuery<Entity> having( Expression<Boolean> restriction );

    SubQuery<Entity> orderBy( Order order );

    SubQuery<Entity> orderBy( List<Order> orders );
}
