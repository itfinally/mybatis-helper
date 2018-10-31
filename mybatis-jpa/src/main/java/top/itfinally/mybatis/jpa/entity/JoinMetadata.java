package top.itfinally.mybatis.jpa.entity;

import top.itfinally.mybatis.jpa.criteria.JoinType;

import java.util.Objects;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/29       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class JoinMetadata {
    private String className;
    private JoinType type;

    public JoinMetadata( String className, JoinType type ) {
        this.className = className;
        this.type = type;
    }

    public String getClassName() {
        return className;
    }

    public JoinType getType() {
        return type;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof JoinMetadata ) ) return false;
        JoinMetadata joinMetadata = ( JoinMetadata ) o;
        return Objects.equals( getClassName(), joinMetadata.getClassName() ) &&
                getType() == joinMetadata.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash( getClassName(), getType() );
    }
}
