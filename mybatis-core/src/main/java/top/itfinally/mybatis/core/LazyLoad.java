package top.itfinally.mybatis.core;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/23       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class LazyLoad<V> {
    private final AtomicBoolean loaded = new AtomicBoolean( false );
    private final Condition condition = new ReentrantLock().newCondition();
    private final Callable<V> callable;
    private final TimeUnit timeUnit;
    private final int wait;

    private volatile V value;

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
            if ( null == value && loaded.compareAndSet( false, true ) ) {
                value = callable.call();
                condition.signalAll();

            } else {
                condition.await( wait, timeUnit );
                if ( null == value ) {
                    throw new LazyLoadFailureException( "Get null value after timeout" );
                }
            }

        } catch ( Exception e ) {
            throw new RuntimeException( "Failure to lazy load object", e );
        }

        return value;
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
