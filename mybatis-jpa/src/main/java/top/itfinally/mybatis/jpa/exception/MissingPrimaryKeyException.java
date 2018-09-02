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
public class MissingPrimaryKeyException extends RuntimeException {
    public MissingPrimaryKeyException() {
    }

    public MissingPrimaryKeyException( String message ) {
        super( message );
    }

    public MissingPrimaryKeyException( String message, Throwable cause ) {
        super( message, cause );
    }

    public MissingPrimaryKeyException( Throwable cause ) {
        super( cause );
    }

    public MissingPrimaryKeyException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
