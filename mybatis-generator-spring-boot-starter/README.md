# mybatis-generator-spring-boot-starter

该项目是 mybatis-generator 的 spring-boot-starter, 支持 jdk 1.7 或 1.7+ 版本

添加 maven 依赖:

```xml
<dependency>
  <groupId>top.itfinally</groupId>
  <artifactId>mybatis-generator-spring-boot-starter</artifactId>
  <version>1.0.0.RELEASE</version>
</dependency>
```

如果存在 spring-boot-parent-starter, 切记添加下列属性, 将 thymeleaf 版本切换为 3.0.9

```xml
<properties>
  <thymeleaf.version>3.0.9.RELEASE</thymeleaf.version>
  <thymeleaf-layout-dialect.version>2.2.0</thymeleaf-layout-dialect.version>
</properties>
```





