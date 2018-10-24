package top.itfinally.mybatis.core;

import com.google.common.collect.Sets;
import top.itfinally.mybatis.core.exception.RuntimeExecutionException;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/2       itfinally       首次创建
 * *********************************************
 *
 * 本类的设计基于 "一旦在数据访问层发生异常并直接传递至 vm 层, 通常都是致命并且会导致当前线程上执行的所有任务失败" 的观点
 * 因而发生异常时, 该线程上的所有本地变量都会被清除
 *
 * </pre>
 */
public class ThreadLocalDelegatedFactory {
    private final static Set<SafetyThreadLocal<?>> threadLocals = Sets.newConcurrentHashSet();
    private final static Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException( Thread t, Throwable e ) {
            ThreadLocalDelegatedFactory.cleanup();
            throw new RuntimeExecutionException( e );
        }
    };

    private ThreadLocalDelegatedFactory() {
    }

    public static <T> ThreadLocal<T> newThreadLocal() {
        ThreadLocal<T> threadLocal = new SafetyThreadLocal<>();
        threadLocals.add( ( SafetyThreadLocal<?> ) threadLocal );

        return threadLocal;
    }

    private static void cleanup() {
        for ( SafetyThreadLocal<?> threadLocal : threadLocals ) {
            threadLocal.remove();
        }
    }
    
    private static class SafetyThreadLocal<T> extends ThreadLocal<T> {
        private static final Class<Void> placeholder = Void.class;
        private final ConcurrentMap<String, Class<Void>> setupThread = new ConcurrentHashMap<>();

        @Override
        public void set( T value ) {
            String currentThreadName = Thread.currentThread().getName();
            if ( !setupThread.containsKey( currentThreadName ) && null == setupThread.putIfAbsent( currentThreadName, placeholder ) ) {
                Thread.currentThread().setUncaughtExceptionHandler( handler );
            }

            super.set( value );
        }
    }
}
