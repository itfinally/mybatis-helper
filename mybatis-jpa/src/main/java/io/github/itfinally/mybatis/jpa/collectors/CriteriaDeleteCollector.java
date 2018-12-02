package io.github.itfinally.mybatis.jpa.collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import io.github.itfinally.mybatis.jpa.criteria.Root;
import io.github.itfinally.mybatis.jpa.criteria.query.AbstractQuery;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;

import java.util.ArrayList;
import java.util.List;

public class CriteriaDeleteCollector extends AbstractCollector {

  private final List<Root<?>> roots = new ArrayList<>();

  public CriteriaDeleteCollector( CriteriaBuilder criteriaBuilder, AbstractQuery<?> owner ) {
    super( criteriaBuilder, owner );
  }

  public void addDeleteRoot( final List<Root<?>> roots ) {
    concurrentChecking( new Runnable() {
      @Override
      public void run() {
        CriteriaDeleteCollector.this.roots.addAll( roots );
      }
    } );
  }

  @Override
  public String toFormatString( ParameterBus parameters ) {
    String deleteClause = completeDeleteRoots( parameters );
    String fromClause = completeRoots( parameters );
    String whereClause = completeConditions( parameters );

    StringBuilder builder = new StringBuilder()
        .append( Strings.isNullOrEmpty( deleteClause ) ? "delete %s " : "delete " )
        .append( fromClause );

    if ( Strings.isNullOrEmpty( whereClause ) ) {
      builder.append( " where " ).append( whereClause );
    }

    return String.format( "<script> %s </script>", builder.toString() );
  }

  private String completeDeleteRoots( ParameterBus parameters ) {
    List<String> deletes = new ArrayList<>();
    String className;

    for ( Root<?> item : roots ) {
      className = item.getModel().getEntityMetadata().getEntityClass().getName();
      if ( !( super.roots.containsKey( className ) || joinMetadata.containsKey( className ) ) ) {
        throw new IllegalStateException( String.format( "Entity '%s' is not in this criteria object", className ) );
      }

      deletes.add( ( ( Writable ) item ).toFormatString( parameters ) );
    }

    return Joiner.on( ", " ).join( deletes );
  }
}
