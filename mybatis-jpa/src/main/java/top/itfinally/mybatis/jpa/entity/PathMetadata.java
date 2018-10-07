package top.itfinally.mybatis.jpa.entity;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/6       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class PathMetadata {
    private final EntityMetadata entityMetadata;
    private final AttributeMetadata attributeMetadata;

    public PathMetadata( EntityMetadata entityMetadata, AttributeMetadata attributeMetadata ) {
        this.entityMetadata = entityMetadata;
        this.attributeMetadata = attributeMetadata;
    }

    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    public AttributeMetadata getAttributeMetadata() {
        return attributeMetadata;
    }

    public boolean isForeignKey() {
        return attributeMetadata instanceof ReferenceMetadata;
    }
}
