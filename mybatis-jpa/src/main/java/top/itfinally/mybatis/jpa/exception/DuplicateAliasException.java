package top.itfinally.mybatis.jpa.exception;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/9/14       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class DuplicateAliasException extends RuntimeException {
    public DuplicateAliasException() {
    }

    public DuplicateAliasException( String message ) {
        super( message );
    }

    public DuplicateAliasException( String message, Throwable cause ) {
        super( message, cause );
    }

    public DuplicateAliasException( Throwable cause ) {
        super( cause );
    }

    public DuplicateAliasException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
