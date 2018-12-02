package io.github.itfinally.mybatis.paging.collection;

import org.springframework.jdbc.core.JdbcTemplate;
import io.github.itfinally.mybatis.paging.PagingItem;

import javax.annotation.Nonnull;
import java.util.*;

public class PagingSet<E> extends AbstractPaging implements Set<E> {
  private final Set<E> originalSet;

  public PagingSet( Set<E> originalSet, PagingItem pagingItem, List<String> countingSql,
                    Object[] orderedArgs, JdbcTemplate jdbcTemplate, int indexStartingWith ) {
    super( pagingItem, countingSql, orderedArgs, jdbcTemplate, indexStartingWith );

    this.originalSet = originalSet;
  }

  @Override
  public int size() {
    return originalSet.size();
  }

  @Override
  public boolean isEmpty() {
    return originalSet.isEmpty();
  }

  @Override
  public boolean contains( Object o ) {
    return originalSet.contains( o );
  }

  @Override
  public @Nonnull
  Iterator<E> iterator() {
    return originalSet.iterator();
  }

  @Override
  public @Nonnull
  Object[] toArray() {
    return originalSet.toArray();
  }

  @Override
  public @Nonnull
  <T> T[] toArray( @Nonnull T[] a ) {
    return originalSet.toArray( a );
  }

  @Override
  public boolean add( E e ) {
    return originalSet.add( e );
  }

  @Override
  public boolean remove( Object o ) {
    return originalSet.remove( o );
  }

  @Override
  public boolean containsAll( @Nonnull Collection<?> c ) {
    return originalSet.containsAll( c );
  }

  @Override
  public boolean addAll( @Nonnull Collection<? extends E> c ) {
    return originalSet.addAll( c );
  }

  @Override
  public boolean retainAll( @Nonnull Collection<?> c ) {
    return originalSet.retainAll( c );
  }

  @Override
  public boolean removeAll( @Nonnull Collection<?> c ) {
    return originalSet.removeAll( c );
  }

  @Override
  public void clear() {
    originalSet.clear();
  }

  @Override
  public boolean equals( Object o ) {
    if ( this == o ) return true;
    if ( !( o instanceof PagingSet ) ) return false;
    PagingSet<?> pagingSet = ( PagingSet<?> ) o;
    return Objects.equals( originalSet, pagingSet.originalSet );
  }

  @Override
  public int hashCode() {
    return Objects.hash( originalSet );
  }

  @Override
  public String toString() {
    return originalSet.toString();
  }
}
