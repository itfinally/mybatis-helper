package top.itfinally.mybatis.generator.core.database.entity;

import java.util.List;
import java.util.Objects;

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
public class UniqueKeyEntity {
    // 唯一键的名称
    private String keyName;

    // 唯一键的成员字段
    private List<ColumnEntity> columns;

    public String getKeyName() {
        return keyName;
    }

    public UniqueKeyEntity setKeyName( String keyName ) {
        this.keyName = keyName;
        return this;
    }

    public List<ColumnEntity> getColumns() {
        return columns;
    }

    public UniqueKeyEntity setColumns( List<ColumnEntity> columns ) {
        this.columns = columns;
        return this;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof UniqueKeyEntity ) ) return false;
        UniqueKeyEntity that = ( UniqueKeyEntity ) o;
        return Objects.equals( getKeyName(), that.getKeyName() ) &&
                Objects.equals( getColumns(), that.getColumns() );
    }

    @Override
    public int hashCode() {

        return Objects.hash( getKeyName(), getColumns() );
    }

    @Override
    public String toString() {
        return "UniqueKeyEntity{" +
                "keyName='" + keyName + '\'' +
                ", columns=" + columns +
                '}';
    }
}
