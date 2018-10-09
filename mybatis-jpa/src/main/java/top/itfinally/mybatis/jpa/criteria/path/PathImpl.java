package top.itfinally.mybatis.jpa.criteria.path;

import top.itfinally.mybatis.jpa.criteria.Path;
import top.itfinally.mybatis.jpa.criteria.Root;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.query.QueryCollector;
import top.itfinally.mybatis.jpa.entity.AttributeMetadata;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.PathMetadata;
import top.itfinally.mybatis.jpa.entity.ReferenceMetadata;
import top.itfinally.mybatis.jpa.exception.NoSuchAttributeException;

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
public class PathImpl<Entity> extends ExpressionImpl<Entity> implements Path<Entity> {

    public PathImpl( CriteriaBuilder criteriaBuilder, QueryCollector queryCollector ) {
        super( criteriaBuilder, queryCollector );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Path<Entity> get( String attributeName ) {
        AttributeMetadata attributeMetadata = getAttribute( getRealType( Root.class, this )
                .getModel().getEntityMetadata(), attributeName );

        return new AttributePath<>( criteriaBuilder(), queryCollector(), ( Root<?> ) this, attributeMetadata );
    }

    protected static AttributeMetadata getAttribute( EntityMetadata entityMetadata, String attributeName ) {
        for ( AttributeMetadata item : entityMetadata.getColumns() ) {
            if ( item.getJavaName().equals( attributeName ) ) {
                return item;
            }
        }

        for ( ReferenceMetadata item : entityMetadata.getReferenceColumns() ) {
            if ( item.getJavaName().equals( attributeName ) ) {
                return item;
            }
        }

        throw new NoSuchAttributeException( String.format( "No such attribute '%s' from entity '%s'",
                attributeName, entityMetadata.getEntityClass().getName() ) );
    }

    @Override
    public PathMetadata getModel() {
        throw new UnsupportedOperationException( "Unsupported get path model before getting specify attribute." );
    }
}
