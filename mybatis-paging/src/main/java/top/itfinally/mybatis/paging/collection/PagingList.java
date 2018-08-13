package top.itfinally.mybatis.paging.collection;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/12       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class PagingList<E> extends AbstractPaging implements List<E> {
    private final List<E> originalList;

    public PagingList( List<E> originalList, String sql, Object[] orderedArgs ) {
        super( sql, orderedArgs );

        this.originalList = originalList;
    }

    @Override
    public int size() {
        return originalList.size();
    }

    @Override
    public boolean isEmpty() {
        return originalList.isEmpty();
    }

    @Override
    public boolean contains( Object o ) {
        return originalList.contains( o );
    }

    @Override
    public @Nonnull Iterator<E> iterator() {
        return originalList.iterator();
    }

    @Override
    public @Nonnull Object[] toArray() {
        return originalList.toArray();
    }

    @Override
    public @Nonnull <T> T[] toArray( @Nonnull T[] a ) {
        return originalList.toArray( a );
    }

    @Override
    public boolean add( E e ) {
        return originalList.add( e );
    }

    @Override
    public boolean remove( Object o ) {
        return originalList.remove( o );
    }

    @Override
    public boolean containsAll( @Nonnull Collection<?> c ) {
        return originalList.containsAll( c );
    }

    @Override
    public boolean addAll( @Nonnull Collection<? extends E> c ) {
        return originalList.addAll( c );
    }

    @Override
    public boolean addAll( int index, @Nonnull Collection<? extends E> c ) {
        return originalList.addAll( index, c );
    }

    @Override
    public boolean removeAll( @Nonnull Collection<?> c ) {
        return originalList.removeAll( c );
    }

    @Override
    public boolean retainAll( @Nonnull Collection<?> c ) {
        return originalList.retainAll( c );
    }

    @Override
    public void clear() {
        originalList.clear();
    }

    @Override
    public E get( int index ) {
        return originalList.get( index );
    }

    @Override
    public E set( int index, E element ) {
        return originalList.set( index, element );
    }

    @Override
    public void add( int index, E element ) {
        originalList.add( index, element );
    }

    @Override
    public E remove( int index ) {
        return originalList.remove( index );
    }

    @Override
    public int indexOf( Object o ) {
        return originalList.indexOf( o );
    }

    @Override
    public int lastIndexOf( Object o ) {
        return originalList.lastIndexOf( o );
    }

    @Override
    public @Nonnull ListIterator<E> listIterator() {
        return originalList.listIterator();
    }

    @Override
    public @Nonnull ListIterator<E> listIterator( int index ) {
        return originalList.listIterator( index );
    }

    @Override
    public @Nonnull List<E> subList( int fromIndex, int toIndex ) {
        return originalList.subList( fromIndex, toIndex );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof PagingList ) ) return false;
        PagingList<?> that = ( PagingList<?> ) o;
        return Objects.equals( originalList, that.originalList );
    }

    @Override
    public int hashCode() {
        return Objects.hash( originalList );
    }

    @Override
    public String toString() {
        return originalList.toString();
    }
}
