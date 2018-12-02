package io.github.itfinally.mybatis.core;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LazyLoad<V> {
  private final AtomicReference<V> value = new AtomicReference<>( null );
  private final Condition condition = new ReentrantLock().newCondition();
  private final Callable<V> callable;
  private final TimeUnit timeUnit;
  private final int wait;

  public LazyLoad( Callable<V> callable ) {
    this( 5, TimeUnit.SECONDS, callable );
  }

  public LazyLoad( int wait, TimeUnit timeUnit, Callable<V> callable ) {
    this.wait = wait;
    this.timeUnit = timeUnit;
    this.callable = callable;
  }

  public V get() {
    try {
      if ( value.get() == null ) {
        value.compareAndSet( null, callable.call() );
        condition.signalAll();

      } else {
        condition.await( wait, timeUnit );
        if ( value.get() == null ) {
          throw new LazyLoadFailureException( "Get null value after timeout" );
        }
      }

    } catch ( Exception e ) {
      throw new RuntimeException( "Failure to lazy load object", e );
    }

    return value.get();
  }

  public static class LazyLoadFailureException extends RuntimeException {
    public LazyLoadFailureException() {
    }

    public LazyLoadFailureException( String message ) {
      super( message );
    }

    public LazyLoadFailureException( String message, Throwable cause ) {
      super( message, cause );
    }

    public LazyLoadFailureException( Throwable cause ) {
      super( cause );
    }

    public LazyLoadFailureException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
      super( message, cause, enableSuppression, writableStackTrace );
    }
  }
}
