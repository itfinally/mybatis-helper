package top.itfinally.mybatis.jpa.exception;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/5       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class FileCreateException extends RuntimeException {
    public FileCreateException() {
    }

    public FileCreateException( String message ) {
        super( message );
    }

    public FileCreateException( String message, Throwable cause ) {
        super( message, cause );
    }

    public FileCreateException( Throwable cause ) {
        super( cause );
    }

    public FileCreateException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
