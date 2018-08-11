package top.itfinally.mybatis.generator.exception;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class LackOfConfigurationException extends RuntimeException {
    public LackOfConfigurationException() {
    }

    public LackOfConfigurationException( String message ) {
        super( message );
    }

    public LackOfConfigurationException( String message, Throwable cause ) {
        super( message, cause );
    }

    public LackOfConfigurationException( Throwable cause ) {
        super( cause );
    }

    public LackOfConfigurationException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
