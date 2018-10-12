package top.itfinally.mybatis.jpa.criteria.query;

import java.util.List;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/9       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public interface DelegatedTypedQuery<Entity> extends DelegatedQuery {

    <O> DelegatedTypedQuery<O> as( Class<O> entityClass );

    List<Entity> getResultList();

    Entity getSingleResult();
}
