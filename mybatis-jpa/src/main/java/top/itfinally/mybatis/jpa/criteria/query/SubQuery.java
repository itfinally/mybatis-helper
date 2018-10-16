package top.itfinally.mybatis.jpa.criteria.query;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Order;
import top.itfinally.mybatis.jpa.criteria.Reference;

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

    SubQuery<Entity> groupBy( Reference<?>... restriction );

    SubQuery<Entity> having( Expression<Boolean>... restriction );

    SubQuery<Entity> orderBy( Order... orders );
}
