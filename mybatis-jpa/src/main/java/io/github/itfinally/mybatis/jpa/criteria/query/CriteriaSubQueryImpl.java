package io.github.itfinally.mybatis.jpa.criteria.query;

import com.google.common.collect.Lists;
import io.github.itfinally.mybatis.jpa.collectors.CriteriaQueryCollector;
import io.github.itfinally.mybatis.jpa.criteria.Expression;
import io.github.itfinally.mybatis.jpa.criteria.Order;
import io.github.itfinally.mybatis.jpa.criteria.Reference;
import io.github.itfinally.mybatis.jpa.criteria.Root;
import io.github.itfinally.mybatis.jpa.criteria.path.ExpressionImpl;
import io.github.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import io.github.itfinally.mybatis.jpa.criteria.render.Writable;

public class CriteriaSubQueryImpl<Entity> extends ExpressionImpl<Entity, CriteriaQueryCollector> implements SubQuery<Entity>, Writable {

  private final AbstractQuery<?> parent;
  private final CriteriaQueryCollector criteriaQueryCollector;

  public CriteriaSubQueryImpl( CriteriaBuilder builder, AbstractQuery<?> parentQuery ) {
    super( builder, null );

    this.parent = parentQuery;
    this.criteriaQueryCollector = new CriteriaQueryCollector( criteriaBuilder(), parentQuery, this );
  }

  @Override
  protected CriteriaQueryCollector queryCollector() {
    return criteriaQueryCollector;
  }

  @Override
  public SubQuery<Entity> select( Reference<?>... path ) {
    queryCollector().addSelection( Lists.newArrayList( path ) );
    return this;
  }

  @Override
  public <X> Root<X> from( Class<X> entityClass ) {
    return queryCollector().from( entityClass );
  }

  @Override
  @SafeVarargs
  public final SubQuery<Entity> where( Expression<Boolean>... restriction ) {
    queryCollector().addCondition( Lists.newArrayList( restriction ) );
    return this;
  }

  @Override
  public SubQuery<Entity> groupBy( Reference<?>... restriction ) {
    queryCollector().addGrouping( Lists.newArrayList( restriction ) );
    return this;
  }

  @Override
  public SubQuery<Entity> having( Expression<Boolean>... restriction ) {
    queryCollector().addHaving( Lists.newArrayList( restriction ) );
    return this;
  }

  @Override
  public SubQuery<Entity> orderBy( Order... orders ) {
    queryCollector().addOrder( Lists.newArrayList( orders ) );
    return this;
  }

  @Override
  public <T> SubQuery<T> subQuery() {
    return queryCollector().subQuery();
  }

  @Override
  public String toFormatString( ParameterBus parameters ) {
    return criteriaQueryCollector.toFormatString( parameters );
  }
}
