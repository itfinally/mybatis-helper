package top.itfinally.mybatis.jpa.exception;

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
public class DuplicatePrimaryKeyException extends RuntimeException {
    public DuplicatePrimaryKeyException() {
    }

    public DuplicatePrimaryKeyException( String message ) {
        super( message );
    }

    public DuplicatePrimaryKeyException( String message, Throwable cause ) {
        super( message, cause );
    }

    public DuplicatePrimaryKeyException( Throwable cause ) {
        super( cause );
    }

    public DuplicatePrimaryKeyException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
