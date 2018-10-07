package top.itfinally.mybatis.jpa.criteria.render;

import top.itfinally.mybatis.jpa.entity.AttributeMetadata;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/4       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class AttributeWrapper {
    private final AttributeMetadata attributeMetadata;
    private final String alias;

    public AttributeWrapper( AttributeMetadata attributeMetadata, String alias ) {
        this.attributeMetadata = attributeMetadata;
        this.alias = alias;
    }

    public AttributeMetadata getAttributeMetadata() {
        return attributeMetadata;
    }

    public String getAlias() {
        return alias;
    }

    public String ofEntityName() {
        return attributeMetadata.getField().getDeclaringClass().getName();
    }
}
