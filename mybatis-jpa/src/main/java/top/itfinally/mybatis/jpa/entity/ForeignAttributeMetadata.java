package top.itfinally.mybatis.jpa.entity;

import java.util.Objects;

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

    private boolean map;
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

    public boolean isMap() {
        return map;
    }

    public ForeignAttributeMetadata setMap( boolean map ) {
        this.map = map;
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof ForeignAttributeMetadata ) ) return false;
        if ( !super.equals( o ) ) return false;
        ForeignAttributeMetadata that = ( ForeignAttributeMetadata ) o;
        return isLazy() == that.isLazy() &&
                isMap() == that.isMap() &&
                isCollection() == that.isCollection() &&
                Objects.equals( getEntityMetadata(), that.getEntityMetadata() ) &&
                Objects.equals( getActualType(), that.getActualType() ) &&
                Objects.equals( getReferenceAttributeMetadata(), that.getReferenceAttributeMetadata() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), getEntityMetadata(), isLazy(), isMap(), isCollection(),
                getActualType(), getReferenceAttributeMetadata() );
    }

    @Override
    public String toString() {
        return "ForeignAttributeMetadata{" +
                "entityMetadata=" + entityMetadata +
                ", isLazy=" + isLazy +
                ", map=" + map +
                ", collection=" + collection +
                ", actualType=" + actualType +
                ", referenceAttributeMetadata=" + referenceAttributeMetadata +
                "} " + super.toString();
    }
}
