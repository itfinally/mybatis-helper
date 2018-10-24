package top.itfinally.mybatis.jpa.criteria.predicate;

import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Predicate;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.Collection;
import java.util.Objects;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/9       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class InPredicate extends AbstractPredicateImpl implements Predicate {

    private final Expression<?> expression;
    private final Object unknownVal;

    public InPredicate( CriteriaBuilder builder, Expression<?> expression, Object unknownVal ) {
        super( builder );

        this.expression = Objects.requireNonNull( expression, "Expression require not null" );
        this.unknownVal = Objects.requireNonNull( unknownVal, "Value require not null" );
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        String inClause;

        if ( unknownVal instanceof Expression<?> ) {
            inClause = ( ( Writable ) unknownVal ).toFormatString( parameters );

        } else if ( unknownVal instanceof Collection || unknownVal.getClass().isArray() ) {
            if ( isEmptyCollectionOrArray( unknownVal ) ) {
                throw new IllegalArgumentException( "The Collection that you given is empty, please check you criteria object statement" );
            }

            String key = parameters.put( unknownVal );

            inClause = String.format( "<foreach collection=\"%s\" item=\"item\" open=\"(\" separator=\",\" close=\")\" >", key ) +
                    "#{item}" +
                    "</foreach>";

        } else {
            throw new IllegalArgumentException( "InPredicate have an unknown type of parameter: " + unknownVal.getClass().getName() );
        }

        return String.format( "%s %s in %s", ( ( Writable ) expression ).toFormatString( parameters ),
                isNegated() ? "not" : "", inClause );
    }

    private static boolean isEmptyCollectionOrArray( Object collection ) {
        return collection instanceof Collection
                ? ( ( Collection ) collection ).isEmpty()
                : ( ( Object[] ) collection ).length <= 0;
    }
}
