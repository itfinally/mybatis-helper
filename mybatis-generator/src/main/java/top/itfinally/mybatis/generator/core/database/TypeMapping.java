package top.itfinally.mybatis.generator.core.database;

import java.util.Objects;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/31       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class TypeMapping {
    private String jdbcType;
    private Class<?> javaType;
    private String javaTypeFullName;

    public String getJdbcType() {
        return jdbcType;
    }

    public TypeMapping setJdbcType( String jdbcType ) {
        this.jdbcType = jdbcType;
        return this;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public TypeMapping setJavaType( Class<?> javaType ) {
        this.javaType = javaType;
        return this;
    }

    public String getJavaTypeFullName() {
        return javaTypeFullName;
    }

    public TypeMapping setJavaTypeFullName( String javaTypeFullName ) {
        this.javaTypeFullName = javaTypeFullName;
        return this;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof TypeMapping ) ) return false;
        TypeMapping that = ( TypeMapping ) o;
        return Objects.equals( getJdbcType(), that.getJdbcType() ) &&
                Objects.equals( getJavaType(), that.getJavaType() ) &&
                Objects.equals( getJavaTypeFullName(), that.getJavaTypeFullName() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( getJdbcType(), getJavaType(), getJavaTypeFullName() );
    }

    @Override
    public String toString() {
        return "TypeMapping{" +
                "jdbcType='" + jdbcType + '\'' +
                ", javaType=" + javaType +
                ", javaTypeFullName='" + javaTypeFullName + '\'' +
                '}';
    }
}
