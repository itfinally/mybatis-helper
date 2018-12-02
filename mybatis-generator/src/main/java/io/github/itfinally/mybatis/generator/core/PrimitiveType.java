package io.github.itfinally.mybatis.generator.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrimitiveType {
  private static final PrimitiveType primitiveType = new PrimitiveType();
  private static List<Method> types;

  static {
    List<Method> types = new ArrayList<>();
    Method[] methods = PrimitiveType.class.getDeclaredMethods();
    for ( Method method : methods ) {
      if ( !"getType".equals( method.getName() ) && method.getName().matches( ".*Type$" ) ) {
        method.setAccessible( true );
        types.add( method );
      }
    }

    PrimitiveType.types = Collections.unmodifiableList( types );
  }

  private PrimitiveType() {
  }

  public static Class<?> getType( Class<?> type ) {
    Object result;
    for ( Method method : types ) {
      try {
        result = method.invoke( primitiveType, type );
        if ( result != null ) {
          return ( Class<?> ) result;
        }

      } catch ( IllegalAccessException | InvocationTargetException e ) {
        throw new RuntimeException( e );
      }
    }

    return null;
  }

  private Class<?> byteType( Class<?> type ) {
    return byte.class == type ? type : Byte.class == type ? byte.class : null;
  }

  private Class<?> booleanType( Class<?> type ) {
    return boolean.class == type ? type : Boolean.class == type ? boolean.class : null;
  }

  private Class<?> shortType( Class<?> type ) {
    return short.class == type ? type : Short.class == type ? short.class : null;
  }

  private Class<?> charType( Class<?> type ) {
    return char.class == type ? type : Character.class == type ? char.class : null;
  }

  private Class<?> intType( Class<?> type ) {
    return int.class == type ? type : Integer.class == type ? int.class : null;
  }

  private Class<?> longType( Class<?> type ) {
    return long.class == type ? type : Long.class == type ? long.class : null;
  }

  private Class<?> floatType( Class<?> type ) {
    return float.class == type ? type : Float.class == type ? float.class : null;
  }

  private Class<?> doubleType( Class<?> type ) {
    return double.class == type ? type : Double.class == type ? double.class : null;
  }
}
