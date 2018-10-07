package top.itfinally.mybatis.jpa.entity;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/9       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ReferenceMetadata extends AttributeMetadata {
    private EntityMetadata entityMetadata;
    private boolean isLazy;

    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    public ReferenceMetadata setEntityMetadata( EntityMetadata entityMetadata ) {
        this.entityMetadata = entityMetadata;
        return this;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public ReferenceMetadata setLazy( boolean lazy ) {
        isLazy = lazy;
        return this;
    }
}
