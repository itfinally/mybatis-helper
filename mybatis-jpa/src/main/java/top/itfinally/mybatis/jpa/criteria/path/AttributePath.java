package top.itfinally.mybatis.jpa.criteria.path;

import top.itfinally.mybatis.jpa.collectors.AbstractCollector;
import top.itfinally.mybatis.jpa.criteria.Path;
import top.itfinally.mybatis.jpa.criteria.Root;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;
import top.itfinally.mybatis.jpa.entity.AttributeMetadata;
import top.itfinally.mybatis.jpa.entity.PathMetadata;
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
public class AttributePath<Entity, Collector extends AbstractCollector> extends PathImpl<Entity, Collector> implements Path<Entity>, Writable {

    private final boolean isRelationToken;
    private final RootImpl<Entity, Collector> root;
    private final AttributeMetadata attributeMetadata;

    @SuppressWarnings( "unchecked" )
    public AttributePath( CriteriaBuilder builder, Collector queryCollector, Root<?> root, AttributeMetadata attributeMetadata ) {
        super( builder, queryCollector );

        this.attributeMetadata = attributeMetadata;
        this.root = ( RootImpl<Entity, Collector> ) root;
        this.isRelationToken = attributeMetadata instanceof ForeignAttributeMetadata;
    }

    @Override
    public PathMetadata getModel() {
        return new PathMetadata( root.getModel().getEntityMetadata(), attributeMetadata );
    }

    @Override
    public Path<Entity> get( String attributeName ) {
        if ( !isRelationToken ) {
            throw new IllegalStateException( String.format( "Attribute '%s' of entity '%s' is not a foreign key ( " +
                    "Maybe you missing declared it by something like @OneToOne annotation ? )",
                    attributeMetadata.getJavaName(), attributeMetadata.getField().getDeclaringClass().getName() ) );
        }

        ForeignAttributeMetadata foreignAttributeMetadata = getRealType( ForeignAttributeMetadata.class, attributeMetadata );
        AttributeMetadata newAttributeMetadata = getAttribute( foreignAttributeMetadata.getEntityMetadata(), attributeName );

        // Have problem if use 'exists' and 'join' with the same table
        Root<?> root = queryCollector().from( foreignAttributeMetadata.getEntityMetadata().getEntityClass() );

        return new AttributePath<>( criteriaBuilder(), queryCollector(), root, newAttributeMetadata );
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        if ( null == root ) {
            String className = attributeMetadata.getField().getDeclaringClass().getName();

            throw new IllegalStateException( String.format( "The attribute is come from entity '%s', but it not found", className ) +
                    "( maybe you forget call 'root.join( ${otherRoot} ).on( builder.equal( ${condition} ) )' instruction )" );
        }

        return String.format( "%s.%s", root.toFormatString( parameters ), attributeMetadata.getJdbcName() );
    }
}
