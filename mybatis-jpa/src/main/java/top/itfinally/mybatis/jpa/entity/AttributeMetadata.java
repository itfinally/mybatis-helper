package top.itfinally.mybatis.jpa.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/28       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class AttributeMetadata {
    private String javaName;
    private String jdbcName;
    private boolean nullable;
    private boolean isPrimary;

    private Field field;
    private Method readMethod;
    private Method writeMethod;

    public String getJavaName() {
        return javaName;
    }

    public AttributeMetadata setJavaName( String javaName ) {
        this.javaName = javaName;
        return this;
    }

    public String getJdbcName() {
        return jdbcName;
    }

    public AttributeMetadata setJdbcName( String jdbcName ) {
        this.jdbcName = jdbcName;
        return this;
    }

    public boolean isNullable() {
        return nullable;
    }

    public AttributeMetadata setNullable( boolean nullable ) {
        this.nullable = nullable;
        return this;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public AttributeMetadata setPrimary( boolean primary ) {
        isPrimary = primary;
        return this;
    }

    public Field getField() {
        return field;
    }

    public AttributeMetadata setField( Field field ) {
        this.field = field;
        return this;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public AttributeMetadata setReadMethod( Method readMethod ) {
        this.readMethod = readMethod;
        return this;
    }

    public Method getWriteMethod() {
        return writeMethod;
    }

    public AttributeMetadata setWriteMethod( Method writeMethod ) {
        this.writeMethod = writeMethod;
        return this;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof AttributeMetadata ) ) return false;
        AttributeMetadata that = ( AttributeMetadata ) o;
        return isNullable() == that.isNullable() &&
                isPrimary() == that.isPrimary() &&
                Objects.equals( getJavaName(), that.getJavaName() ) &&
                Objects.equals( getJdbcName(), that.getJdbcName() ) &&
                Objects.equals( getField(), that.getField() ) &&
                Objects.equals( getReadMethod(), that.getReadMethod() ) &&
                Objects.equals( getWriteMethod(), that.getWriteMethod() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( getJavaName(), getJdbcName(), isNullable(), isPrimary(), getField(),
                getReadMethod(), getWriteMethod() );
    }

    @Override
    public String toString() {
        return "AttributeMetadata{" +
                "javaName='" + javaName + '\'' +
                ", jdbcName='" + jdbcName + '\'' +
                ", nullable=" + nullable +
                ", isPrimary=" + isPrimary +
                ", field=" + field +
                ", readMethod=" + readMethod +
                ", writeMethod=" + writeMethod +
                '}';
    }
}
