package top.itfinally.mybatis.jpa.entity;

import java.io.InputStream;

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
public class ResultMapMetadata {
    private InputStream inputStream;
    private String methodName;
    private String className;
    private String filePath;
    private String hashKey;
    private String id;

    public InputStream getInputStream() {
        return inputStream;
    }

    public ResultMapMetadata setInputStream( InputStream inputStream ) {
        this.inputStream = inputStream;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public ResultMapMetadata setMethodName( String methodName ) {
        this.methodName = methodName;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public ResultMapMetadata setClassName( String className ) {
        this.className = className;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public ResultMapMetadata setFilePath( String filePath ) {
        this.filePath = filePath;
        return this;
    }

    public String getHashKey() {
        return hashKey;
    }

    public ResultMapMetadata setHashKey( String hashKey ) {
        this.hashKey = hashKey;
        return this;
    }

    public String getId() {
        return id;
    }

    public ResultMapMetadata setId( String id ) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "ResultMapMetadata{" +
                "inputStream=" + inputStream +
                ", methodName='" + methodName + '\'' +
                ", className='" + className + '\'' +
                ", filePath='" + filePath + '\'' +
                ", hashKey='" + hashKey + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String toMethodString() {
        return String.format( "%s.%s", className, methodName );
    }
}
