package top.itfinally.mybatis.jpa.criteria.query;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Root;

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
public interface CriteriaDelete<Entity> extends AbstractQuery<Entity> {

    @Override
    CriteriaDelete<Entity> where( Expression<Boolean>... restriction );

    CriteriaDelete<Entity> delete( Root<?>... roots );
}
