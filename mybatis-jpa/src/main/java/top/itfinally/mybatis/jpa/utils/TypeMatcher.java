package top.itfinally.mybatis.jpa.utils;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/15       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public enum TypeMatcher {
    ;

    private static final Set<Class<?>> basicTypeFilter = Sets.newHashSet( Arrays.asList(
            boolean.class, Boolean.class, char.class, Character.class,
            short.class, Short.class, int.class, Integer.class,
            long.class, Long.class, float.class, Float.class,
            double.class, Double.class, void.class, Void.class,
            String.class
    ) );

    public static boolean isBasicType( Class<?> type ) {
        return basicTypeFilter.contains( type );
    }

    public static boolean hasNullValueInCollection( Collection<?> collection ) {
        for ( Object item : collection ) {
            if ( null == item ) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasNullValueInArray( Object[] array ) {
        for ( Object item : array ) {
            if ( null == item ) {
                return true;
            }
        }

        return false;
    }
}
