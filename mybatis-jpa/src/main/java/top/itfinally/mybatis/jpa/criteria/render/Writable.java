package top.itfinally.mybatis.jpa.criteria.render;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/4       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public interface Writable {
    String toFormatString( ParameterBus parameters );
}
