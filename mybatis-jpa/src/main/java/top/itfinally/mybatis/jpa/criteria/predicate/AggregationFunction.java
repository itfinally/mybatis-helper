package top.itfinally.mybatis.jpa.criteria.predicate;

import com.google.common.base.Joiner;
import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Root;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.ArrayList;
import java.util.List;

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
public class AggregationFunction<Value> extends AbstractFunctionExpressionImpl<Value> implements Expression<Value> {

    public AggregationFunction( CriteriaBuilder criteriaBuilder, String functionName, Object... parameters ) {
        super( criteriaBuilder, functionName, parameters );
    }

    public static class COUNT extends AggregationFunction<Long> {
        private static final String NAME = "count";

        private final Object[] parameters;
        private final boolean distinct;

        public COUNT( CriteriaBuilder criteriaBuilder, boolean distinct, Object parameters ) {
            super( criteriaBuilder, NAME, parameters );

            this.parameters = new Object[]{ parameters };
            this.distinct = distinct;
        }

        @Override
        public String toFormatString( ParameterBus parameters ) {
            List<String> args = new ArrayList<>();

            for ( Object item : this.parameters ) {
                if ( item instanceof Root ) {
                    args.add( "*" );

                } else if ( item instanceof Expression ) {
                    args.add( ( ( Writable ) item ).toFormatString( parameters ) );

                } else {
                    args.add( String.format( "'%s'", item ) );
                }
            }

            return String.format( "%s( %s %s )", NAME, distinct ? "distinct" : "", Joiner.on( ", " ).join( args ) );
        }
    }

    public static class MAX<T extends Number> extends AggregationFunction<T> {
        private static final String NAME = "max";

        public MAX( CriteriaBuilder criteriaBuilder, Object parameters ) {
            super( criteriaBuilder, NAME, parameters );
        }
    }

    public static class MIN<T extends Number> extends AggregationFunction<T> {
        private static final String NAME = "min";

        public MIN( CriteriaBuilder criteriaBuilder, Object parameters ) {
            super( criteriaBuilder, NAME, parameters );
        }
    }

    public static class SUM<T extends Number> extends AggregationFunction<T> {
        private static final String NAME = "sum";

        public SUM( CriteriaBuilder criteriaBuilder, Object parameters ) {
            super( criteriaBuilder, NAME, parameters );
        }
    }

    public static class AVG<T extends Number> extends AggregationFunction<T> {
        private static final String NAME = "avg";

        public AVG( CriteriaBuilder criteriaBuilder, Object parameters ) {
            super( criteriaBuilder, NAME, parameters );
        }
    }
}
