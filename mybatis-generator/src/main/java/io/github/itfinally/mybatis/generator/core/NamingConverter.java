package io.github.itfinally.mybatis.generator.core;

import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;
import io.github.itfinally.mybatis.generator.configuration.MybatisGeneratorProperties;

@Component
public class NamingConverter {

  private boolean isConvertToCamel;

  public NamingConverter( MybatisGeneratorProperties properties ) {
    isConvertToCamel = properties.isConvertToCamel();
  }

  public String convert( String name, boolean isFirstCharLower ) {
    return isConvertToCamel ? findUnderLineAndCaseIt( name, isFirstCharLower ) : name;
  }

  private String findUnderLineAndCaseIt( String name, boolean isFirstCharLower ) {
    String[] names = name.replaceAll( "^_", "" ).split( "_" );
    for ( int index = names.length - 1, end = isFirstCharLower ? 1 : 0; index >= end; index -= 1 ) {
      names[ index ] = names[ index ].replaceAll( "^\\w", Character.toString( names[ index ].charAt( 0 ) ).toUpperCase() );
    }

    return Joiner.on( "" ).join( names );
  }
}
