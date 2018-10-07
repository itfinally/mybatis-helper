package top.itfinally.mybatis.jpa.criteria;

import java.util.Collection;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/29       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public interface Expression<T> extends Reference<T> {

    Predicate isNull();

    Predicate isNotNull();

    // Expression or real value
    Predicate in( Expression<?> value );

    // Expression or real values
    Predicate in( Collection<?> values );

}
