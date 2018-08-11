package top.itfinally.mybatis.generator.core;

import org.springframework.stereotype.Component;
import top.itfinally.mybatis.generator.configuration.MybatisGeneratorProperties;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/31       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Component
public class NamingConverter {

    private boolean isConvertToCamel;

    public NamingConverter( MybatisGeneratorProperties properties ) {
        isConvertToCamel = properties.isConvertToCamel();
    }

    public String convert( String name ) {
        return isConvertToCamel ? findUnderLineAndCaseIt( name ) : name;
    }

    private String findUnderLineAndCaseIt( String name ) {
        String[] names = name.replaceAll( "^_", "" ).split( "_" );
        for ( int index = names.length - 1; index > 0; index -= 1 ) {
            names[ index ] = names[ index ].replaceAll( "^\\w", Character.toString( names[ index ].charAt( 0 ) ).toUpperCase() );
        }

        return joinWithBlack( names );
    }

    private String joinWithBlack( String[] arr ) {
        StringBuilder buffer = new StringBuilder();

        for ( String item : arr ) {
            buffer.append( item );
        }

        return buffer.toString();
    }
}
