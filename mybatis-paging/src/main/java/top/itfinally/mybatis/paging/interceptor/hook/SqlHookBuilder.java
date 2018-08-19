package top.itfinally.mybatis.paging.interceptor.hook;

import net.sf.jsqlparser.JSQLParserException;

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
public interface SqlHookBuilder {
    SqlHook build( String originSql, long beginRow, long range ) throws JSQLParserException;
}
