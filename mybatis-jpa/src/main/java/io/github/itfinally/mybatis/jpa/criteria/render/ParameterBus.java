package io.github.itfinally.mybatis.jpa.criteria.render;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ParameterBus extends ConcurrentHashMap<String, Object> implements ConcurrentMap<String, Object> {

  public static final String PREFIX = "jpa-parameter-";

  private final AtomicInteger step = new AtomicInteger( 0 );

  public String put( Object value ) {
    int index = step.getAndIncrement();
    String key = String.format( PREFIX + index, index );

    put( key, value );

    return key;
  }
}
