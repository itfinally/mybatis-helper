package top.itfinally.mybatis.paging.collection;

import com.google.common.collect.Lists;
import org.springframework.jdbc.core.JdbcTemplate;
import top.itfinally.mybatis.paging.PagingItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final Pattern matching = Pattern.compile( "(\\?)+" );

    private final List<SqlUnit> sqlUnits;
    private final PagingItem pagingItem;
    private final int indexStartingWith;

    private volatile JdbcTemplate jdbcTemplate;
    private volatile long count;

    AbstractPaging( PagingItem pagingItem, List<String> countingSql, Object[] orderedArgs, JdbcTemplate jdbcTemplate, int indexStartingWith ) {
        int index = 0, length;
        List<SqlUnit> sqlUnits = new ArrayList<>();
        List<Object> args = Lists.newArrayList( orderedArgs );

        for ( String sql : countingSql ) {
            length = countingArgs( sql );
            sqlUnits.add( new SqlUnit( sql, args.subList( index, length ).toArray() ) );

            index += length;
        }

        this.sqlUnits = sqlUnits;
        this.pagingItem = pagingItem;
        this.jdbcTemplate = jdbcTemplate;
        this.indexStartingWith = indexStartingWith;
    }

    private void counting() {
        if ( jdbcTemplate != null ) {
            synchronized ( this ) {
                if ( jdbcTemplate != null ) {
                    Integer totalCount = 0, count;
                    for ( SqlUnit item : sqlUnits ) {
                        count = jdbcTemplate.queryForObject( item.sql, Integer.class, item.orderedArgs );

                        if ( null == count ) {
                            throw new IllegalStateException( String.format( "Failure to counting, result is null. " +
                                    "countingSql: %s", item.sql ) );
                        }

                        totalCount += count;
                    }

                    this.count = totalCount;
                    jdbcTemplate = null;
                }
            }
        }
    }

    @Override
    public final long getPage() {
        counting();
        return count / pagingItem.getRange() + indexStartingWith;
    }

    @Override
    public final long getCount() {
        counting();
        return count;
    }

    @Override
    public final long getCurrentPage() {
        return pagingItem.getBeginRow() / pagingItem.getRange();
    }

    private int countingArgs( String sql ) {
        Matcher matcher = matching.matcher( sql );

        int count = 0;
        while ( matcher.find() ) {
            count += 1;
        }

        return count;
    }

    private static class SqlUnit {
        private final String sql;
        private final Object[] orderedArgs;

        SqlUnit( String sql, Object[] orderedArgs ) {
            this.sql = sql;
            this.orderedArgs = orderedArgs;
        }
    }
}
