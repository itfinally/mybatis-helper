package top.itfinally.mybatis.paging.collection;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

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
public class PagingSet<E> extends AbstractPaging implements Set<E> {
    private final Set<E> originalSet;

    public PagingSet( Set<E> originalSet, String sql ) {
        super( sql );

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
    public @Nonnull Iterator<E> iterator() {
        return originalSet.iterator();
    }

    @Override
    public @Nonnull Object[] toArray() {
        return originalSet.toArray();
    }

    @Override
    public @Nonnull <T> T[] toArray( @Nonnull T[] a ) {
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


}
