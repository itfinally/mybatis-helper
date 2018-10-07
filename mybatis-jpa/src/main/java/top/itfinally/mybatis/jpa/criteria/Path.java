package top.itfinally.mybatis.jpa.criteria;

import top.itfinally.mybatis.jpa.entity.PathMetadata;

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
public interface Path<Entity> extends Expression<Entity> {

    PathMetadata getModel();

    Path<Entity> get( String attributeName );

}
