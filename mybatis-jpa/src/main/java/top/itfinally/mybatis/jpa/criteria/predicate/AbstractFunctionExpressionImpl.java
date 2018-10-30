package top.itfinally.mybatis.jpa.criteria.predicate;

import com.google.common.base.Joiner;
import top.itfinally.mybatis.jpa.collectors.AbstractCollector;
import top.itfinally.mybatis.jpa.criteria.Expression;
import top.itfinally.mybatis.jpa.criteria.Path;
import top.itfinally.mybatis.jpa.criteria.path.AttributePath;
import top.itfinally.mybatis.jpa.criteria.path.ExpressionImpl;
import top.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.criteria.render.Writable;
import top.itfinally.mybatis.jpa.utils.TypeMatcher;

import java.util.ArrayList;
import java.util.List;
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
public abstract class AbstractFunctionExpressionImpl<T> extends ExpressionImpl<T, AbstractCollector>
        implements Expression<T>, Writable {

    private final String functionName;
    private final Object[] parameters;

    public AbstractFunctionExpressionImpl( CriteriaBuilder criteriaBuilder, String functionName, Object[] parameters ) {

        super( criteriaBuilder, null );

        this.functionName = Objects.requireNonNull( functionName, "Function name require not null" );
        this.parameters = parameters;
    }

    @Override
    protected AbstractCollector queryCollector() {
        throw new UnsupportedOperationException( "Do not calling collector in simple expression." );
    }

    public List<Path<?>> getPaths() {
        List<Path<?>> paths = new ArrayList<>();

        for( Object item : parameters ) {
            if ( item instanceof AttributePath ) {
                paths.add( ( Path<?> ) item );
            }
        }

        return paths;
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
        List<String> args = new ArrayList<>();

        for ( Object item : this.parameters ) {
            args.add( item instanceof Expression
                    ? ( ( Writable ) item ).toFormatString( parameters )
                    : TypeMatcher.isNumeric( item.getClass() ) ? item.toString()
                    : String.format( "'%s'", item ) );
        }

        return String.format( "%s( %s )", functionName, Joiner.on( ", " ).join( args ) );
    }
}
