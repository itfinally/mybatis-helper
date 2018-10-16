package top.itfinally.mybatis.jpa.criteria.predicate;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Predicate;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.Objects;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/14       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class LikePredicate extends AbstractPredicateImpl implements Predicate {

    private final Expression<?> expression;
    private final Object unknownPattern;

    public LikePredicate( CriteriaBuilder builder, Expression<?> expression, Object unknownPattern ) {
        super( builder );

        this.expression = Objects.requireNonNull( expression, "Expression require not null" );
        this.unknownPattern = Objects.requireNonNull( unknownPattern, "Pattern require not null" );
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        String likeClause;

        if ( unknownPattern instanceof String ) {
            likeClause = String.format( "#{%s}", parameters.put( ( ( String ) unknownPattern ).trim() ) );

        } else if( unknownPattern instanceof Expression ) {
            likeClause = ( ( Writable ) unknownPattern ).toFormatString( parameters );

        } else {
            throw new IllegalArgumentException( "LikePredicate have an unknown type of parameter: " + unknownPattern.getClass().getName() );
        }

        return String.format( "%s %s like %s", ( ( Writable ) expression ).toFormatString( parameters ),
                isNegated() ? "not" : "", likeClause );
    }
}
