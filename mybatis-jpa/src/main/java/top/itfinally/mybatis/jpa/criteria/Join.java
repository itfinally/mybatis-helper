package top.itfinally.mybatis.jpa.criteria;

import java.util.List;

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
public interface Join<Entity> extends From<Entity> {

    Join<Entity> on( Predicate restrictions );

    Join<Entity> on( List<Predicate> restrictions );

}
