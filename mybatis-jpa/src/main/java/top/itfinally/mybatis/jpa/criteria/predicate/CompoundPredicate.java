package top.itfinally.mybatis.jpa.criteria.predicate;

import com.google.common.base.Joiner;
import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Predicate;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;
import top.itfinally.mybatis.jpa.utils.TypeMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/15       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class CompoundPredicate extends AbstractPredicateImpl implements Predicate {

    private final List<Predicate> expressions;
    private final AtomicReference<LogicalConjunction> operation;

    public CompoundPredicate( CriteriaBuilder builder, LogicalConjunction logicalConjunction, List<Predicate> expressions ) {
        super( builder );

        this.operation = new AtomicReference<>( logicalConjunction );
        this.expressions = Objects.requireNonNull( expressions, "Expressions require not null" );

        if ( TypeMatcher.hasNullValueInCollection( expressions ) ) {
            throw new NullPointerException( "There are have null value inside the given collection" );
        }
    }

    @Override
    public Predicate not() {
        LogicalConjunction conjunction = this.operation.get();
        this.operation.compareAndSet( conjunction, LogicalConjunction.AND == conjunction
                ? LogicalConjunction.OR : LogicalConjunction.AND );

        return super.not();
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        List<String> conditions = new ArrayList<>();
        List<Predicate> expressions = this.expressions;

        if ( expressions.isEmpty() ) {
            return "";

        } else if ( 1 == expressions.size() ) {

            // If There are have one time to found expressions just have single element,
            // then maybe there are have more than one collection what just have single element, I guess.
            // So found all nest predicate and extract until found one expressions what have more than one element.
            while ( 1 == expressions.size() && expressions.get( 0 ) instanceof CompoundPredicate ) {
                CompoundPredicate predicate = ( CompoundPredicate ) expressions.get( 0 );

                // If only one predicate exists and also it instance of CompoundPredicate, then extract expressions.
                // And use operation of the new CompoundPredicate.
                expressions = predicate.expressions;
            }

            if ( 1 == expressions.size() && !( expressions.get( 0 ) instanceof CompoundPredicate ) ) {
                // There are no more expressions but just one, so formatting in direct.
                return ( ( Writable ) expressions.get( 0 ) ).toFormatString( parameters );
            }
        }

        for ( Expression<Boolean> item : expressions ) {
            conditions.add( ( ( Writable ) item ).toFormatString( parameters ) );
        }

        return String.format( " ( %s ) ", Joiner.on( operation.get() == LogicalConjunction.AND
                ? " and " : " or " ).join( conditions ) );
    }

    public enum LogicalConjunction {
        AND, OR
    }
}
