package top.itfinally.mybatis.paging.collection;

import org.springframework.jdbc.core.JdbcTemplate;
import top.itfinally.mybatis.paging.PagingItem;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/12       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public abstract class AbstractPaging implements BasicPaging {
    private final String sql;
    private final Object[] orderedArgs;
    private final PagingItem pagingItem;

    private volatile JdbcTemplate jdbcTemplate;
    private volatile double count;

    AbstractPaging( PagingItem pagingItem, String sql, Object[] orderedArgs, JdbcTemplate jdbcTemplate ) {
        this.sql = sql;
        this.pagingItem = pagingItem;
        this.orderedArgs = orderedArgs;
        this.jdbcTemplate = jdbcTemplate;
    }

    private void counting() {
        if ( jdbcTemplate != null ) {
            synchronized ( this ) {
                if ( jdbcTemplate != null ) {
                    Integer count = jdbcTemplate.queryForObject( sql, Integer.class, orderedArgs );
                    if ( null == count ) {
                        throw new IllegalStateException( String.format( "Failure to counting, result is null. sql: %s", sql ) );
                    }

                    this.count = count;
                    jdbcTemplate = null;
                }
            }
        }
    }

    @Override
    public final long getPage() {
        counting();
        return ( long ) Math.ceil( count / pagingItem.getRange() );
    }

    @Override
    public final long getCount() {
        counting();
        return ( long ) count;
    }

    @Override
    public final long getCurrentPage() {
        return pagingItem.getBeginRow() / pagingItem.getRange();
    }
}
