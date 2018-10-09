package top.itfinally.mybatis.jpa.criteria.path;

import top.itfinally.mybatis.jpa.criteria.Path;
import top.itfinally.mybatis.jpa.criteria.Root;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.query.QueryCollector;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;
import top.itfinally.mybatis.jpa.entity.AttributeMetadata;
import top.itfinally.mybatis.jpa.entity.PathMetadata;
import top.itfinally.mybatis.jpa.entity.ReferenceMetadata;

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
public class AttributePath<Entity> extends PathImpl<Entity> implements Path<Entity>, Writable {

    private final AttributeMetadata attributeMetadata;
    private final boolean isRelationToken;

    private final RootImpl<Entity> root;

    @SuppressWarnings( "unchecked" )
    public AttributePath( CriteriaBuilder builder, QueryCollector queryCollector, Root<?> root, AttributeMetadata attributeMetadata ) {
        super( builder, queryCollector );

        this.root = ( RootImpl<Entity> ) root;
        this.attributeMetadata = attributeMetadata;
        this.isRelationToken = attributeMetadata instanceof ReferenceMetadata;
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

        ReferenceMetadata referenceMetadata = getRealType( ReferenceMetadata.class, attributeMetadata );
        AttributeMetadata newAttributeMetadata = getAttribute( referenceMetadata.getEntityMetadata(), attributeName );

        // Have problem if use 'exists' and 'join' with the same table
        Root<?> root = queryCollector().from( referenceMetadata.getEntityMetadata().getEntityClass() );

        return new AttributePath<>( criteriaBuilder(), queryCollector(), root, newAttributeMetadata );
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
//        if ( null == root ) {
//            String className = attributeMetadata.getField().getDeclaringClass().getName();
//
//            throw new IllegalStateException( String.format( "the attribute is come from entity '%s', but it not found", className ) +
//                    "( maybe you forget call 'root.join( ${otherRoot} ).on( builder.equal( ${condition} ) )' instruction )" );
//        }

        return String.format( "%s.%s", root.toFormatString( parameters ), attributeMetadata.getJdbcName() );
    }
}
