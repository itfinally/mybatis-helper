package io.github.itfinally.mybatis.jpa.collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import io.github.itfinally.mybatis.jpa.criteria.Path;
import io.github.itfinally.mybatis.jpa.criteria.predicate.ValueWrapper;
import io.github.itfinally.mybatis.jpa.criteria.query.AbstractQuery;
import io.github.itfinally.mybatis.jpa.criteria.query.CriteriaBuilder;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;
import io.github.itfinally.mybatis.jpa.entity.AttributeMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaUpdateCollector extends AbstractCollector {

  private final Map<AttributeMetadata, SetterEntry> setterMapping = new HashMap<>();

  public CriteriaUpdateCollector( CriteriaBuilder criteriaBuilder, AbstractQuery<?> owner ) {
    super( criteriaBuilder, owner );
  }

  public void addSetter( final Path<?> path, final ValueWrapper wrapper ) {
    concurrentChecking( new Runnable() {
      @Override
      public void run() {
        setterMapping.put( path.getModel().getAttributeMetadata(), new SetterEntry( path, wrapper ) );
      }
    } );
  }

  @Override
  public String toFormatString( final ParameterBus parameters ) {
    if ( setterMapping.isEmpty() || roots.isEmpty() ) {
      throw new IllegalStateException( "There are not Update clause or Set clause found" );
    }

    return concurrentChecking( new Supplier<String>() {
      @Override
      public String get() {
        String updateClause = completeRoots( parameters );
        String setterClause = completeUpdates( parameters );
        String whereClause = completeConditions( parameters );

        StringBuilder builder = new StringBuilder( String.format( "update %s set %s", updateClause, setterClause ) );

        if ( !Strings.isNullOrEmpty( whereClause ) ) {
          builder.append( " where " ).append( whereClause );
        }

        if ( subQuery ) {
          throw new IllegalStateException( "Sql of update operation cannot be a subQuery" );
        }

        return String.format( "<script> %s </script>", builder.toString() );
      }
    } );
  }

  private String completeUpdates( ParameterBus parameters ) {
    List<String> setters = new ArrayList<>();

    for ( SetterEntry item : setterMapping.values() ) {
      setters.add( item.toFormatString( parameters ) );
    }

    return Joiner.on( ", " ).join( setters );
  }

  private static class SetterEntry implements Writable {
    private Path<?> path;
    private ValueWrapper wrapper;

    private SetterEntry( Path<?> path, ValueWrapper wrapper ) {
      this.path = path;
      this.wrapper = wrapper;
    }

    @Override
    public String toFormatString( ParameterBus parameters ) {
      return String.format( "%s = %s", ( ( Writable ) path ).toFormatString( parameters ),
          wrapper.toFormatString( parameters ) );
    }
  }
}
