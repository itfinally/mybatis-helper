package top.itfinally.mybatis.jpa.exception;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/10       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class NoSuchActualTypeException extends RuntimeException {
    public NoSuchActualTypeException() {
    }

    public NoSuchActualTypeException( String message ) {
        super( message );
    }

    public NoSuchActualTypeException( String message, Throwable cause ) {
        super( message, cause );
    }

    public NoSuchActualTypeException( Throwable cause ) {
        super( cause );
    }

    public NoSuchActualTypeException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
