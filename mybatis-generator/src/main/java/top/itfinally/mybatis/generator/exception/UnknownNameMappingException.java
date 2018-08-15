package top.itfinally.mybatis.generator.exception;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/31       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class UnknownNameMappingException extends RuntimeException {
    public UnknownNameMappingException() {
    }

    public UnknownNameMappingException( String message ) {
        super( message );
    }

    public UnknownNameMappingException( String message, Throwable cause ) {
        super( message, cause );
    }

    public UnknownNameMappingException( Throwable cause ) {
        super( cause );
    }

    public UnknownNameMappingException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
