package top.itfinally.mybatis.generator.core.database.entity;

import java.util.Objects;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ColumnEntity {
    // 字段描述
    private String comment;

    // 表字段名
    private String jdbcName;

    // java 实体属性名
    private String javaName;

    // 表字段类型
    private String jdbcType;

    // java 属性类型名
    private String javaType;

    // 属性能否为空
    private boolean notNull;

    // 是否唯一键
    private boolean uniqueKey;

    // 是否主键
    private boolean primaryKey;

    // java 属性类型类对象
    private Class<?> javaTypeClass;

    // java 属性 getter 名
    private String getterName;

    // java 属性 setter 名
    private String setterName;

    // java 实体的父类是否有该属性
    private boolean hidden;

    public String getJdbcName() {
        return jdbcName;
    }

    public ColumnEntity setJdbcName( String jdbcName ) {
        this.jdbcName = jdbcName;
        return this;
    }

    public String getJavaName() {
        return javaName;
    }

    public ColumnEntity setJavaName( String javaName ) {
        this.javaName = javaName;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public ColumnEntity setComment( String comment ) {
        this.comment = comment;
        return this;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public ColumnEntity setJdbcType( String jdbcType ) {
        this.jdbcType = jdbcType;
        return this;
    }

    public String getJavaType() {
        return javaType;
    }

    public ColumnEntity setJavaType( String javaType ) {
        this.javaType = javaType;
        return this;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public ColumnEntity setNotNull( boolean notNull ) {
        this.notNull = notNull;
        return this;
    }

    public boolean isUniqueKey() {
        return uniqueKey;
    }

    public ColumnEntity setUniqueKey( boolean uniqueKey ) {
        this.uniqueKey = uniqueKey;
        return this;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public ColumnEntity setPrimaryKey( boolean primaryKey ) {
        this.primaryKey = primaryKey;
        return this;
    }

    public Class<?> getJavaTypeClass() {
        return javaTypeClass;
    }

    public ColumnEntity setJavaTypeClass( Class<?> javaTypeClass ) {
        this.javaTypeClass = javaTypeClass;
        return this;
    }

    public String getGetterName() {
        return getterName;
    }

    public ColumnEntity setGetterName( String getterName ) {
        this.getterName = getterName;
        return this;
    }

    public String getSetterName() {
        return setterName;
    }

    public ColumnEntity setSetterName( String setterName ) {
        this.setterName = setterName;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public ColumnEntity setHidden( boolean hidden ) {
        this.hidden = hidden;
        return this;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof ColumnEntity ) ) return false;
        ColumnEntity that = ( ColumnEntity ) o;
        return isNotNull() == that.isNotNull() &&
                isUniqueKey() == that.isUniqueKey() &&
                isPrimaryKey() == that.isPrimaryKey() &&
                isHidden() == that.isHidden() &&
                Objects.equals( getComment(), that.getComment() ) &&
                Objects.equals( getJdbcName(), that.getJdbcName() ) &&
                Objects.equals( getJavaName(), that.getJavaName() ) &&
                Objects.equals( getJdbcType(), that.getJdbcType() ) &&
                Objects.equals( getJavaType(), that.getJavaType() ) &&
                Objects.equals( getJavaTypeClass(), that.getJavaTypeClass() ) &&
                Objects.equals( getGetterName(), that.getGetterName() ) &&
                Objects.equals( getSetterName(), that.getSetterName() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( getComment(), getJdbcName(), getJavaName(), getJdbcType(), getJavaType(), isNotNull(), isUniqueKey(),
                isPrimaryKey(), getJavaTypeClass(), getGetterName(), getSetterName(), isHidden() );
    }

    @Override
    public String toString() {
        return "ColumnEntity{" +
                "comment='" + comment + '\'' +
                ", jdbcName='" + jdbcName + '\'' +
                ", javaName='" + javaName + '\'' +
                ", jdbcType='" + jdbcType + '\'' +
                ", javaType='" + javaType + '\'' +
                ", notNull=" + notNull +
                ", uniqueKey=" + uniqueKey +
                ", primaryKey=" + primaryKey +
                ", javaTypeClass=" + javaTypeClass +
                ", getterName='" + getterName + '\'' +
                ", setterName='" + setterName + '\'' +
                ", hidden=" + hidden +
                '}';
    }
}
