package io.github.itfinally.mybatis.jpa.criteria.predicate;

import com.google.common.base.Joiner;
import io.github.itfinally.mybatis.jpa.collectors.AbstractCollector;
import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Path;
import io.github.itfinally.mybatis.jpa.criteria.path.AttributePath;
import io.github.itfinally.mybatis.jpa.criteria.path.ExpressionImpl;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;
import io.github.itfinally.mybatis.jpa.utils.TypeMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    for ( Object item : parameters ) {
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
