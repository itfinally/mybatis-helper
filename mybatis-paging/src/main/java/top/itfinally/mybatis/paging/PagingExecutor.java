package top.itfinally.mybatis.paging;

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
public class PagingExecutor {
    private static final ThreadLocal<PagingItem> pagingLocal = new ThreadLocal<>();

    public static void pagingAsPage( long page, long range ) {
        if ( page < 0 ) {
            throw new IllegalArgumentException( "Page must greater than zero." );
        }

        if ( range < 0 ) {
            throw new IllegalArgumentException( "Range must greater than zero." );
        }

        pagingAsRow( page * range, range, false );
    }

    public static void pagingAsPage( long page, long range, boolean holding ) {
        if ( page < 0 ) {
            throw new IllegalArgumentException( "Page must greater than zero." );
        }

        if ( range < 0 ) {
            throw new IllegalArgumentException( "Range must greater than zero." );
        }

        pagingAsRow( page * range, range, holding );
    }

    public static void pagingAsRow( long beginRow, long range ) {
        if ( beginRow < 0 ) {
            throw new IllegalArgumentException( "BeginRow must greater than zero." );
        }

        if ( range < 0 ) {
            throw new IllegalArgumentException( "Range must greater than zero." );
        }

        pagingLocal.set( new PagingItem( beginRow, range, false ) );
    }

    public static void pagingAsRow( long beginRow, long range, boolean holding ) {
        if ( beginRow < 0 ) {
            throw new IllegalArgumentException( "BeginRow must greater than zero." );
        }

        if ( range < 0 ) {
            throw new IllegalArgumentException( "Range must greater than zero." );
        }

        pagingLocal.set( new PagingItem( beginRow, range, holding ) );
    }

    public static void clear() {
        pagingLocal.remove();
    }

    static PagingItem getPagingItem() {
        return pagingLocal.get();
    }
}
