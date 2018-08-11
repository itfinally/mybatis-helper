package top.itfinally.mybatis.generator.exception;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/31       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class UnknownTypeException extends RuntimeException {
    public UnknownTypeException() {
    }

    public UnknownTypeException( String message ) {
        super( message );
    }

    public UnknownTypeException( String message, Throwable cause ) {
        super( message, cause );
    }

    public UnknownTypeException( Throwable cause ) {
        super( cause );
    }

    public UnknownTypeException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
