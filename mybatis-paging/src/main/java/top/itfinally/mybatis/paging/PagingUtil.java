package top.itfinally.mybatis.paging;

import org.apache.ibatis.cursor.Cursor;
import top.itfinally.mybatis.paging.collection.AbstractPaging;

import java.util.Collection;
import java.util.Map;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/19       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public final class PagingUtil {
    private PagingUtil() {
    }

    public static long getTotalPages( Collection<?> collection ) {
        return collection instanceof AbstractPaging ? ( ( AbstractPaging ) collection ).getPage() : 0;
    }

    public static long getTotalCount( Collection<?> collection ) {
        return collection instanceof AbstractPaging ? ( ( AbstractPaging ) collection ).getCount() : 0;
    }

    public static long getCurrentPage( Collection<?> collection ) {
        return collection instanceof AbstractPaging ? ( ( AbstractPaging ) collection ).getCurrentPage() : 0;
    }

    public static long getTotalPages( Map<?, ?> map ) {
        return map instanceof AbstractPaging ? ( ( AbstractPaging ) map ).getPage() : 0;
    }

    public static long getTotalCount( Map<?, ?> map ) {
        return map instanceof AbstractPaging ? ( ( AbstractPaging ) map ).getCount() : 0;
    }

    public static long getCurrentPage( Map<?, ?> map ) {
        return map instanceof AbstractPaging ? ( ( AbstractPaging ) map ).getCurrentPage() : 0;
    }

    public static long getTotalPages( Cursor<?> cursor ) {
        return cursor instanceof AbstractPaging ? ( ( AbstractPaging ) cursor ).getPage() : 0;
    }

    public static long getTotalCount( Cursor<?> cursor ) {
        return cursor instanceof AbstractPaging ? ( ( AbstractPaging ) cursor ).getCount() : 0;
    }

    public static long getCurrentPage( Cursor<?> cursor ) {
        return cursor instanceof AbstractPaging ? ( ( AbstractPaging ) cursor ).getCurrentPage() : 0;
    }
}
