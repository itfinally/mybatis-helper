package top.itfinally.mybatis.jpa.criteria.render;

import top.itfinally.mybatis.jpa.entity.EntityMetadata;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/4       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class RootWrapper {
    private final EntityMetadata entityMetadata;
    private final String alias;

    public RootWrapper( EntityMetadata entityMetadata, String alias ) {
        this.entityMetadata = entityMetadata;
        this.alias = alias;
    }

    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    public String getAlias() {
        return alias;
    }
}
