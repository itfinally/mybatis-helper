package io.github.itfinally.mybatis.generator.core.job;

public class JobUnit {
  private String className;
  private String writePath;
  private String packageName;
  private Class<?> superClass;

  // 输出文件时, 对应的文件存在多个空白行时会被合并成 blankLine 行
  private int blankLine;

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

  public Class<?> getSuperClass() {
    return superClass;
  }

  public JobUnit setSuperClass( Class<?> superClass ) {
    this.superClass = superClass;
    return this;
  }

  public int getBlankLine() {
    return blankLine;
  }

  public JobUnit setBlankLine( int blankLine ) {
    this.blankLine = blankLine;
    return this;
  }

  @Override
  public String toString() {
    return "JobUnit{" +
        "className='" + className + '\'' +
        ", writePath='" + writePath + '\'' +
        ", packageName='" + packageName + '\'' +
        ", superClass=" + superClass +
        ", blankLine=" + blankLine +
        '}';
  }
}
