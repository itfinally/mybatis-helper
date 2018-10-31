package top.itfinally.mybatis.jpa.criteria.path;

import top.itfinally.mybatis.jpa.collectors.AbstractCollector;
import top.itfinally.mybatis.jpa.criteria.*;
import top.itfinally.mybatis.jpa.entity.JoinMetadata;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.entity.AttributeMetadata;
import top.itfinally.mybatis.jpa.entity.ForeignAttributeMetadata;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class FromImpl<Entity, Collector extends AbstractCollector> extends PathImpl<Entity, Collector> implements From<Entity> {

    public FromImpl( CriteriaBuilder criteriaBuilder, Collector queryCollector ) {
        super( criteriaBuilder, queryCollector );
    }

    @Override
    public Join<Entity> join( String attributeName ) {
        return join( attributeName, JoinType.INNER );
    }

    @Override
    public Join<Entity> join( String attributeName, JoinType jt ) {
        AttributeMetadata attributeMetadata = getRealType( Path.class, this )
                .get( attributeName ).getModel().getAttributeMetadata();

        if ( !( attributeMetadata instanceof ForeignAttributeMetadata ) ) {
            throw new IllegalStateException( String.format( "Attribute '%s' of entity '%s' is not a foreign key ( " +
                            "Maybe you missing declared it by something like @OneToOne annotation ? )",
                    attributeMetadata.getJavaName(), attributeMetadata.getField().getDeclaringClass().getName() ) );
        }

        queryCollector().addJoiner( new JoinMetadata( getRealType( ForeignAttributeMetadata.class, attributeMetadata )
                .getEntityMetadata().getEntityClass().getName(), jt ) );

        return new JoinImpl<>( criteriaBuilder(), queryCollector(), this );
    }
}
