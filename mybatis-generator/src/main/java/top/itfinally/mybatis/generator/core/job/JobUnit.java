package top.itfinally.mybatis.generator.core.job;

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
public class JobUnit {
    private String className;
    private String writePath;
    private String packageName;
    private String templatePath;
    private Class<?> superClass;

    // 输出文件时, 对应的文件存在多个空白行时会被合并成 blackLine 行
    private int blackLine;

    public String getClassName() {
        return className;
    }

    public JobUnit setClassName( String className ) {
        this.className = className;
        return this;
    }

    public String getWritePath() {
        return writePath;
    }

    public JobUnit setWritePath( String writePath ) {
        this.writePath = writePath;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public JobUnit setPackageName( String packageName ) {
        this.packageName = packageName;
        return this;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public JobUnit setTemplatePath( String templatePath ) {
        this.templatePath = templatePath;
        return this;
    }

    public Class<?> getSuperClass() {
        return superClass;
    }

    public JobUnit setSuperClass( Class<?> superClass ) {
        this.superClass = superClass;
        return this;
    }

    public int getBlackLine() {
        return blackLine;
    }

    public JobUnit setBlackLine( int blackLine ) {
        this.blackLine = blackLine;
        return this;
    }

    @Override
    public String toString() {
        return "JobUnit{" +
                "className='" + className + '\'' +
                ", writePath='" + writePath + '\'' +
                ", packageName='" + packageName + '\'' +
                ", templatePath='" + templatePath + '\'' +
                ", superClass=" + superClass +
                ", blackLine=" + blackLine +
                '}';
    }
}
