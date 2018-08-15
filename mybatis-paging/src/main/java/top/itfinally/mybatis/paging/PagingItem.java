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
public final class PagingItem {
    private final boolean holding;
    private final long beginRow;
    private final long range;

    PagingItem( long beginRow, long range, boolean holding ) {
        this.holding = holding;
        this.beginRow = beginRow;
        this.range = range;
    }

    public boolean isHolding() {
        return holding;
    }

    public long getBeginRow() {
        return beginRow;
    }

    public long getRange() {
        return range;
    }

    @Override
    public String toString() {
        return "PagingItem{" +
                "holding=" + holding +
                ", beginRow=" + beginRow +
                ", range=" + range +
                '}';
    }
}
