package top.itfinally.mybatis.paging.collection;

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
public abstract class AbstractPaging implements BasicPaging {
    private final String sql;

    public AbstractPaging( String sql ) {
        this.sql = sql;
    }

    @Override
    public long getPage() {
        return 0;
    }

    @Override
    public long getCount() {
        return 0;
    }
}
