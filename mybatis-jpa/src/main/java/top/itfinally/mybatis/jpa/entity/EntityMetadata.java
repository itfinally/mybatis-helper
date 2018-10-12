package top.itfinally.mybatis.jpa.entity;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/24       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class EntityMetadata {
    private String tableName;
    private Class<?> entityClass;
    private AttributeMetadata id;
    private List<AttributeMetadata> columns;
    private List<ForeignAttributeMetadata> referenceColumns;

    public String getTableName() {
        return tableName;
    }

    public EntityMetadata setTableName( String tableName ) {
        this.tableName = tableName;
        return this;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public EntityMetadata setEntityClass( Class<?> entityClass ) {
        this.entityClass = entityClass;
        return this;
    }

    public AttributeMetadata getId() {
        return id;
    }

    public EntityMetadata setId( AttributeMetadata id ) {
        this.id = id;
        return this;
    }

    public List<AttributeMetadata> getColumns() {
        return columns;
    }

    public EntityMetadata setColumns( List<AttributeMetadata> columns ) {
        this.columns = columns;
        return this;
    }

    public List<ForeignAttributeMetadata> getReferenceColumns() {
        return referenceColumns;
    }

    public EntityMetadata setReferenceColumns( List<ForeignAttributeMetadata> referenceColumns ) {
        this.referenceColumns = referenceColumns;
        return this;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof EntityMetadata ) ) return false;
        EntityMetadata metadata = ( EntityMetadata ) o;
        return Objects.equals( getTableName(), metadata.getTableName() ) &&
                Objects.equals( getEntityClass(), metadata.getEntityClass() ) &&
                Objects.equals( getId(), metadata.getId() ) &&
                Objects.equals( getColumns(), metadata.getColumns() ) &&
                Objects.equals( getReferenceColumns(), metadata.getReferenceColumns() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( getTableName(), getEntityClass(), getId(), getColumns(), getReferenceColumns() );
    }
}
