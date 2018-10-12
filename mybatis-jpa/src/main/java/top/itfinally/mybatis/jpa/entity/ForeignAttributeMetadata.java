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
public class ForeignAttributeMetadata extends AttributeMetadata {
    private EntityMetadata entityMetadata;
    private boolean isLazy;

    private boolean collection;
    private Class<?> actualType;
    private AttributeMetadata referenceAttributeMetadata;

    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    public ForeignAttributeMetadata setEntityMetadata( EntityMetadata entityMetadata ) {
        this.entityMetadata = entityMetadata;
        return this;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public ForeignAttributeMetadata setLazy( boolean lazy ) {
        isLazy = lazy;
        return this;
    }

    public boolean isCollection() {
        return collection;
    }

    public ForeignAttributeMetadata setCollection( boolean collection ) {
        this.collection = collection;
        return this;
    }

    public Class<?> getActualType() {
        return actualType;
    }

    public ForeignAttributeMetadata setActualType( Class<?> actualType ) {
        this.actualType = actualType;
        return this;
    }

    public AttributeMetadata getReferenceAttributeMetadata() {
        return referenceAttributeMetadata;
    }

    public ForeignAttributeMetadata setReferenceAttributeMetadata( AttributeMetadata referenceAttributeMetadata ) {
        this.referenceAttributeMetadata = referenceAttributeMetadata;
        return this;
    }
}
