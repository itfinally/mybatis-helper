package top.itfinally.mybatis.core.exception;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/2       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class RuntimeExecutionException extends RuntimeException {
    public RuntimeExecutionException() {
        super();
    }

    public RuntimeExecutionException( String message ) {
        super( message );
    }

    public RuntimeExecutionException( String message, Throwable cause ) {
        super( message, cause );
    }

    public RuntimeExecutionException( Throwable cause ) {
        super( cause );
    }

    protected RuntimeExecutionException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
