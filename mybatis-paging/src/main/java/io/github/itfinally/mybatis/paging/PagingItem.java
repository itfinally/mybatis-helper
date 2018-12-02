package io.github.itfinally.mybatis.paging;

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
