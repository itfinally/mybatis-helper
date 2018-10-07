package top.itfinally.mybatis.jpa.exception;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class NoSuchAttributeException extends RuntimeException {
    public NoSuchAttributeException() {
    }

    public NoSuchAttributeException( String message ) {
        super( message );
    }

    public NoSuchAttributeException( String message, Throwable cause ) {
        super( message, cause );
    }

    public NoSuchAttributeException( Throwable cause ) {
        super( cause );
    }

    public NoSuchAttributeException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
