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
 *  v1.0          2018/9/29       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public interface CriteriaQuery<Entity> extends AbstractQuery<Entity>, AbstractSubQuery {

    CriteriaQuery<Entity> select( Reference<?>... path );

    @Override
    CriteriaQuery<Entity> where( Expression<Boolean>... restrictions );

    CriteriaQuery<Entity> groupBy( Reference<?>... restriction );

    CriteriaQuery<Entity> having( Expression<Boolean>... restriction );

    CriteriaQuery<Entity> orderBy( Order... orders );
}
