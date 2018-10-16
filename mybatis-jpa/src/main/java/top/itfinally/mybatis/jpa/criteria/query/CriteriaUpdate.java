package top.itfinally.mybatis.jpa.criteria.query;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Path;

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
public interface CriteriaUpdate<Entity> extends AbstractQuery<Entity>, AbstractSubQuery {

    CriteriaUpdate<Entity> set( Path<Entity> path, Expression<?> value );

    CriteriaUpdate<Entity> set( Path<Entity> path, Object value );

    @Override
    CriteriaUpdate<Entity> where( Expression<Boolean>... restriction );
}
