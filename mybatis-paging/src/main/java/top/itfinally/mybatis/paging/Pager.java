package top.itfinally.mybatis.paging;

import top.itfinally.mybatis.paging.interceptor.PagingLocal;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/15       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public final class Pager extends PagingLocal implements AutoCloseable {
    private static final Pager pager = new Pager();
    private static final ThreadLocal<PagingItem> pagingLocal = new ThreadLocal<>();
    private static final Thread.UncaughtExceptionHandler handler = new ThreadLocalReleaseHandler();

    static {
        // 最终逃逸到 PagingInterceptor
        setPagingLocal( pagingLocal );
    }

    private Pager() {
    }

    public static void pagingAsPage( long page, long range ) {
        if ( page < getIndexStartingWith() ) {
            throw new IllegalArgumentException( String.format( "Page must greater than %s.", getIndexStartingWith() ) );
        }

        if ( range < 0 ) {
            throw new IllegalArgumentException( "Range must greater than 0." );
        }

        pagingAsRow( getPage( page ) * range, range, false );
    }

    public static void pagingAsPage( long page, long range, boolean holding ) {
        if ( page < getIndexStartingWith() ) {
            throw new IllegalArgumentException( String.format( "Page must greater than %s.", getIndexStartingWith() ) );
        }

        if ( range < 0 ) {
            throw new IllegalArgumentException( "Range must greater than 0." );
        }

        pagingAsRow( getPage( page ) * range, range, holding );
    }

    public static void pagingAsRow( long beginRow, long range ) {
        if ( beginRow < 0 ) {
            throw new IllegalArgumentException( "BeginRow must greater than 0." );
        }

        if ( range < 0 ) {
            throw new IllegalArgumentException( "Range must greater than 0." );
        }

        pagingLocal.set( new PagingItem( beginRow, range, false ) );
    }

    public static void pagingAsRow( long beginRow, long range, boolean holding ) {
        if ( beginRow < 0 ) {
            throw new IllegalArgumentException( "BeginRow must greater than 0." );
        }

        if ( range < 0 ) {
            throw new IllegalArgumentException( "Range must greater than 0." );
        }

        if ( holding && !( Thread.getDefaultUncaughtExceptionHandler() instanceof ThreadLocalReleaseHandler ) ) {
            Thread.setDefaultUncaughtExceptionHandler( handler );
        }

        pagingLocal.set( new PagingItem( beginRow, range, holding ) );
    }

    public static void clear() {
        pagingLocal.remove();
    }

    private static long getPage( long page ) {
        return page - getIndexStartingWith() < 0 ? 0 : page - getIndexStartingWith();
    }

    public static Pager getInstance() {
        return pager;
    }

    @Override
    public void close() {
        clear();
    }

    // 谁异常, 这里的线程就是谁, 如果是 main 线程, 是由 jvm 处理, 这里不会被回调
    // 因为 main 线程异常等同于程序退出
    private static class ThreadLocalReleaseHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException( Thread t, Throwable e ) {
            pagingLocal.remove();

            // 继续往外抛
            throw new RuntimeException( e );
        }
    }
}
