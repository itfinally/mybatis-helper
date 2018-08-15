package top.itfinally.mybatis.generator.core.database.entity;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/7       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ReferenceKeyEntity {
    // 外键名
    private String keyName;

    // 外键字段所在数据库的名称
    private String databaseName;

    // 外键字段所在表的名称
    private TableEntity table;

    // 外键字段的名称
    private ColumnEntity column;

    // 外键关联字段所在表的名称
    private TableEntity referenceTable;

    // 外键关联字段的名称
    private ColumnEntity referenceColumn;

    // 外键关联字段所在的数据库的名称
    private String referenceDatabaseName;

    public String getKeyName() {
        return keyName;
    }

    public ReferenceKeyEntity setKeyName( String keyName ) {
        this.keyName = keyName;
        return this;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public ReferenceKeyEntity setDatabaseName( String databaseName ) {
        this.databaseName = databaseName;
        return this;
    }

    public String getReferenceDatabaseName() {
        return referenceDatabaseName;
    }

    public ReferenceKeyEntity setReferenceDatabaseName( String referenceDatabaseName ) {
        this.referenceDatabaseName = referenceDatabaseName;
        return this;
    }

    public TableEntity getTable() {
        return table;
    }

    public ReferenceKeyEntity setTable( TableEntity table ) {
        this.table = table;
        return this;
    }

    public ColumnEntity getColumn() {
        return column;
    }

    public ReferenceKeyEntity setColumn( ColumnEntity column ) {
        this.column = column;
        return this;
    }

    public TableEntity getReferenceTable() {
        return referenceTable;
    }

    public ReferenceKeyEntity setReferenceTable( TableEntity referenceTable ) {
        this.referenceTable = referenceTable;
        return this;
    }

    public ColumnEntity getReferenceColumn() {
        return referenceColumn;
    }

    public ReferenceKeyEntity setReferenceColumn( ColumnEntity referenceColumn ) {
        this.referenceColumn = referenceColumn;
        return this;
    }
}
