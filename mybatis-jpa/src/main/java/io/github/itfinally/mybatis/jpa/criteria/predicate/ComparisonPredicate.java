package io.github.itfinally.mybatis.jpa.criteria.predicate;

import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.Predicate;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.Objects;

public class ComparisonPredicate extends AbstractPredicateImpl implements Predicate {

  private final LogicOperation operator;
  private final Expression<?> leftVal;
  private final ValueWrapper rightVal;

  public ComparisonPredicate( CriteriaBuilder builder, LogicOperation operator, Expression<?> left, Object right ) {
    super( builder );

    Objects.requireNonNull( left, "ComparisonPredicate expect given non-null value but got null at left value" );
    Objects.requireNonNull( right, "ComparisonPredicate expect given non-null value but got null at right value" );

    this.operator = operator;
    this.rightVal = new ValueWrapper( criteriaBuilder(), right );
    this.leftVal = Objects.requireNonNull( left, "Expression require not null" );
  }

  @Override
  public String toFormatString( ParameterBus parameters ) {
    LogicOperation operator = isNegated() ? this.operator.reverse() : this.operator;
    return String.format( "%s %s %s", ( ( Writable ) leftVal ).toFormatString( parameters ),
        operator.val(), rightVal.toFormatString( parameters ) );
  }

  public enum LogicOperation {
    EQUAL {
      @Override
      public LogicOperation reverse() {
        return NOT_EQUAL;
      }

      @Override
      public String val() {
        return "=";
      }
    },

    NOT_EQUAL {
      @Override
      public LogicOperation reverse() {
        return EQUAL;
      }

      @Override
      public String val() {
        return "!=";
      }
    },

    GREATER_THAN {
      @Override
      public LogicOperation reverse() {
        return LESS_THAN_OR_EQUAL;
      }

      @Override
      public String val() {
        return ">";
      }
    },

    GREATER_THAN_OR_EQUAL {
      @Override
      public LogicOperation reverse() {
        return LESS_THAN;
      }

      @Override
      public String val() {
        return ">=";
      }
    },

    LESS_THAN {
      @Override
      public LogicOperation reverse() {
        return GREATER_THAN_OR_EQUAL;
      }

      @Override
      public String val() {
        return "<";
      }
    },

    LESS_THAN_OR_EQUAL {
      @Override
      public LogicOperation reverse() {
        return GREATER_THAN;
      }

      @Override
      public String val() {
        return "<=";
      }
    };

    public abstract LogicOperation reverse();

    public abstract String val();
  }
}
