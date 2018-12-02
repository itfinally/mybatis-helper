package io.github.itfinally.mybatis.jpa.collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import io.github.itfinally.mybatis.jpa.criteria.*;
import io.github.itfinally.mybatis.jpa.criteria.path.AttributePath;
import io.github.itfinally.mybatis.jpa.criteria.path.RootImpl;
import io.github.itfinally.mybatis.jpa.criteria.predicate.AbstractFunctionExpressionImpl;
import io.github.itfinally.mybatis.jpa.criteria.query.AbstractQuery;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;
import io.github.itfinally.mybatis.jpa.entity.PathMetadata;
import io.github.itfinally.mybatis.jpa.utils.TypeMatcher;

import java.util.*;

public class CriteriaQueryCollector extends AbstractCollector {
  // Selections
  private List<Reference<?>> selections = new ArrayList<>();

  // Group by , having and order by
  private final List<Expression<?>> havings = new ArrayList<>();
  private final List<Reference<?>> groups = new ArrayList<>();
  private final List<Order> orders = new ArrayList<>();

  public CriteriaQueryCollector( CriteriaBuilder criteriaBuilder, AbstractQuery<?> owner ) {
    super( criteriaBuilder, owner );
  }

  public CriteriaQueryCollector( CriteriaBuilder criteriaBuilder, AbstractQuery<?> parent, AbstractQuery<?> owner ) {
    super( criteriaBuilder, parent, owner );
  }

  public void addSelection( final Collection<Reference<?>> selections ) {
    concurrentChecking( new Runnable() {
      @Override
      public void run() {
        CriteriaQueryCollector.this.selections.addAll( selections );
      }
    } );
  }

  public void addGrouping( final Collection<Reference<?>> paths ) {
    if ( TypeMatcher.hasNullValueInCollection( paths ) ) {
      throw new NullPointerException( "There are have null value inside the given collection" );
    }

    concurrentChecking( new Runnable() {
      @Override
      public void run() {
        groups.addAll( paths );
      }
    } );
  }

  public void addHaving( final Collection<Expression<Boolean>> predicates ) {
    if ( TypeMatcher.hasNullValueInCollection( predicates ) ) {
      throw new NullPointerException( "There are have null value inside the given collection" );
    }

    concurrentChecking( new Runnable() {
      @Override
      public void run() {
        havings.addAll( predicates );
      }
    } );
  }

  public void addOrder( final List<Order> orders ) {
    if ( TypeMatcher.hasNullValueInCollection( orders ) ) {
      throw new NullPointerException( "There are have null value inside the given collection" );
    }

    concurrentChecking( new Runnable() {
      @Override
      public void run() {
        CriteriaQueryCollector.this.orders.addAll( orders );
      }
    } );
  }

  // Sql builder

  @Override
  public String toFormatString( final ParameterBus parameters ) {
    if ( selections.isEmpty() || roots.isEmpty() ) {
      throw new IllegalStateException( "There are not Select clause or From clause found" );
    }

    return concurrentChecking( new Supplier<String>() {
      @Override
      public String get() {
        String selectionClause = completeSelections( parameters );
        String fromClause = completeRoots( parameters );
        String whereClause = completeConditions( parameters );
        String groupClause = completeGrouping( parameters );
        String havingClause = completeHaving( parameters );
        String orderClause = completeOrdering( parameters );

        StringBuilder sql = new StringBuilder( String.format( "select %s from %s", selectionClause, fromClause ) );
        if ( !Strings.isNullOrEmpty( whereClause ) ) {
          sql.append( " where " ).append( whereClause );
        }

        if ( !Strings.isNullOrEmpty( groupClause ) ) {
          sql.append( " group by " ).append( groupClause );
        }

        if ( !Strings.isNullOrEmpty( havingClause ) ) {
          sql.append( " having " ).append( havingClause );
        }

        if ( !Strings.isNullOrEmpty( orderClause ) ) {
          sql.append( " order by " ).append( orderClause );
        }

        // mybatis sql script mark
        return subQuery
            ? String.format( "( %s ) ", sql.toString() )
            : String.format( "<script> %s </script>", sql.toString() );
      }
    } );
  }

  private String completeSelections( ParameterBus parameters ) {
    Set<String> rootNames = new HashSet<>();
    List<String> selectionStrings = new ArrayList<>();

    if ( selections.isEmpty() ) {
      throw new IllegalStateException( "There are no columns to selected" );
    }

    // only roots
    for ( Reference<?> item : selections ) {
      if ( !( item instanceof Root ) ) {
        continue;
      }

      rootNames.add( ( ( RootImpl<?, ?> ) item ).getModel().getEntityMetadata().getEntityClass().getName() );
      selectionStrings.add( String.format( "%s.*", ( ( RootImpl<?, ?> ) item ).toFormatString( parameters ) ) );
    }

    // only attributes
    String attributeString;
    for ( Reference<?> item : selections ) {
      if ( item instanceof Root ) {
        continue;
      }

      if ( item instanceof AttributePath ) {
        PathMetadata model = ( ( AttributePath ) item ).getModel();

        if ( rootNames.contains( model.getEntityMetadata().getEntityClass().getName() ) ) {
          throw new IllegalStateException( String.format( "The entity '%s' where attribute '%s' is present already exists",
              model.getAttributeMetadata().getJavaName(), model.getEntityMetadata().getEntityClass().getName() ) );
        }
      }

      if ( item instanceof AbstractFunctionExpressionImpl ) {
        for ( Path<?> path : ( ( AbstractFunctionExpressionImpl<?> ) item ).getPaths() ) {
          if ( path instanceof AttributePath ) {
            PathMetadata model = path.getModel();

            if ( rootNames.contains( model.getEntityMetadata().getEntityClass().getName() ) ) {
              throw new IllegalStateException( String.format( "The entity '%s' where attribute '%s' is present already exists",
                  model.getAttributeMetadata().getJavaName(), model.getEntityMetadata().getEntityClass().getName() ) );
            }
          }
        }
      }

      attributeString = ( ( Writable ) item ).toFormatString( parameters );
      if ( !Strings.isNullOrEmpty( item.getAlias() ) ) {
        attributeString += String.format( " as %s", item.getAlias() );
      }

      selectionStrings.add( attributeString );
    }

    return Joiner.on( ", " ).join( selectionStrings );
  }

  private String completeGrouping( ParameterBus parameters ) {
    List<String> groups = new ArrayList<>();

    for ( Reference<?> item : this.groups ) {
      groups.add( ( ( Writable ) item ).toFormatString( parameters ) );
    }

    return Joiner.on( ", " ).join( groups );
  }

  private String completeHaving( ParameterBus parameters ) {
    List<String> havings = new ArrayList<>();

    for ( Expression<?> item : this.havings ) {
      havings.add( ( ( Writable ) item ).toFormatString( parameters ) );
    }

    return Joiner.on( " and " ).join( havings );
  }

  private String completeOrdering( ParameterBus parameters ) {
    List<String> orders = new ArrayList<>();

    for ( Order item : this.orders ) {
      orders.add( ( ( Writable ) item ).toFormatString( parameters ) );
    }

    return Joiner.on( ", " ).join( orders );
  }
}
