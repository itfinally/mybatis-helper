package io.github.itfinally.mybatis.generator.core.database.entity;

import java.util.*;

public class TableEntity {
  // 数据库表的名称
  private String jdbcName;

  // java 实体的名称
  private String javaName;

  // 数据库表的描述
  private String comment;

  // 数据库表所有的行
  private List<ColumnEntity> columns;

  // java 实体依赖的所有对象的全限定名
  private Set<String> depends = new HashSet<>();

  // 数据库表的主键
  private List<ColumnEntity> primaryKeys = new ArrayList<>();

  // 数据库表所有的唯一键
  private List<UniqueKeyEntity> uniqueKeys = new ArrayList<>();

  // 数据库表所有的外键
  private List<ReferenceKeyEntity> referenceKeys = new ArrayList<>();

  public String getJdbcName() {
    return jdbcName;
  }

  public TableEntity setJdbcName( String jdbcName ) {
    this.jdbcName = jdbcName;
    return this;
  }

  public String getJavaName() {
    return javaName;
  }

  public TableEntity setJavaName( String javaName ) {
    this.javaName = javaName;
    return this;
  }

  public String getComment() {
    return comment;
  }

  public TableEntity setComment( String comment ) {
    this.comment = comment;
    return this;
  }

  public List<ColumnEntity> getColumns() {
    return columns;
  }

  public TableEntity setColumns( List<ColumnEntity> columns ) {
    this.columns = columns;
    return this;
  }

  public Set<String> getDepends() {
    return depends;
  }

  public TableEntity addDepend( String depend ) {
    depends.add( depend );
    return this;
  }

  public List<ColumnEntity> getPrimaryKeys() {
    return primaryKeys;
  }

  public TableEntity addPrimaryKeys( ColumnEntity primaryKey ) {
    primaryKeys.add( primaryKey );
    return this;
  }

  public List<UniqueKeyEntity> getUniqueKeys() {
    return uniqueKeys;
  }

  public TableEntity addUniqueKeys( UniqueKeyEntity uniqueKey ) {
    uniqueKeys.add( uniqueKey );
    return this;
  }

  public List<ReferenceKeyEntity> getReferenceKeys() {
    return referenceKeys;
  }

  public TableEntity addReferenceKeys( ReferenceKeyEntity referenceKey ) {
    referenceKeys.add( referenceKey );
    return this;
  }

  public List<String> getColumnNames() {
    List<String> names = new ArrayList<>();
    for ( ColumnEntity item : columns ) {
      names.add( item.getJavaName() );
    }

    return names;
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof TableEntity ) ) return false;
    TableEntity that = ( TableEntity ) o;
    return Objects.equals( getJdbcName(), that.getJdbcName() ) &&
        Objects.equals( getJavaName(), that.getJavaName() ) &&
        Objects.equals( getComment(), that.getComment() ) &&
        Objects.equals( getColumns(), that.getColumns() ) &&
        Objects.equals( getDepends(), that.getDepends() ) &&
        Objects.equals( getPrimaryKeys(), that.getPrimaryKeys() ) &&
        Objects.equals( getUniqueKeys(), that.getUniqueKeys() ) &&
        Objects.equals( getReferenceKeys(), that.getReferenceKeys() );
  }

  @Override
  public int hashCode() {
    return Objects.hash( getJdbcName(), getJavaName(), getComment(), getColumns(), getDepends(),
        getPrimaryKeys(), getUniqueKeys(), getReferenceKeys() );
  }

  @Override
  public String toString() {
    return "TableEntity{" +
        "jdbcName='" + jdbcName + '\'' +
        ", javaName='" + javaName + '\'' +
        ", comment='" + comment + '\'' +
        ", columns=" + columns +
        ", depends=" + depends +
        ", primaryKeys=" + primaryKeys +
        ", uniqueKeys=" + uniqueKeys +
        ", referenceKeys=" + referenceKeys +
        '}';
  }
}
