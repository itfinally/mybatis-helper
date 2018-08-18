package top.itfinally.mybatis.paging.interceptor.hook;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/14       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public interface SqlHook {
    String getPagingSql();

    String getCountingSql();
}
