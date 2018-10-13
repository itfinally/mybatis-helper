package top.itfinally.mybatis.jpa.exception;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/13       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ConfusionTypeException extends RuntimeException {
    public ConfusionTypeException() {
    }

    public ConfusionTypeException( String message ) {
        super( message );
    }

    public ConfusionTypeException( String message, Throwable cause ) {
        super( message, cause );
    }

    public ConfusionTypeException( Throwable cause ) {
        super( cause );
    }

    public ConfusionTypeException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
