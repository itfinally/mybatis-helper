# Mybatis-Generator

Mybatis-Generator 是一款为 Mybatis 而设计的代码生成器, 使用 thymeleaf 作为模版引擎, 支持 yml 配置, 在默认情况下只需要少量配置即可生成代码。

内置模版仅包括 mybatis 的 entity.java / mapper.java 以及 mapper.xml, 针对 services.java / controller.java 等生成需要外部提供模版。

<strong>目前仅支持 MySQL 数据库。</strong>

### 添加 maven 依赖
```xml
<dependency>
  <groupId>io.github.itfinally</groupId>
  <artifactId>mybatis-generator-spring-boot-starter</artifactId>
  <version>0.1.2.RELEASE</version>
</dependency>
```

如果存在 spring-boot-parent-starter, 切记添加下列属性, 将 thymeleaf 版本切换为 3.0.9

```xml
<properties>
  <thymeleaf.version>3.0.9.RELEASE</thymeleaf.version>
  <thymeleaf-layout-dialect.version>2.2.0</thymeleaf-layout-dialect.version>
</properties>
```

### 使用方式

只需要配置好必填参数即可, 然后继承 `io.github.itfinally.mybatis.generator.MybatisGeneratorRunner` 以普通的 spring-boot 项目启动即可

### 配置
参数配置均以 `mybatis.generator` 开头, 即 `mybatis.generator.xxx`, 下面参数以 yml 文件为例。

##### 必填参数

- <strong>file-parent-path</strong>
  生成文件的父级路径, 默认从项目根目录开始

- <strong>java-file-path</strong>
  生成 Java 文件的相对于 file-parent-path 的路径
  
- <strong>resources-path</strong>
  生成 Mapper.xml 文件的相对于 file-parent-path 的路径
  
- <strong>basic-package</strong>
  基础包名, 如 aaa.bbb.ccc
  
- <strong>entity-package</strong>
  实体包名, 相对于 basic-package, 如填写 ddd, 即 "${basic-package}.ddd"
  
- <strong>repository-package</strong>
  Mapper 类的包名, 相对于 basic-package, 如填写 ddd, 即 "${basic-package}.ddd"

##### 选填参数

- <strong>entity-template-name</strong>
  实体类的模版名, 当需要覆盖默认的模版时使用该参数, 填写相对于 resources 的相对路径, 如 /template/entity.txt
  
- <strong>repository-template-name</strong>
  Mapper 类的模版名, 当需要覆盖默认的模版时使用该参数, 填写相对于 resources 的相对路径, 如 /template/repository.txt
  
- <strong>mapper-xml-template-name</strong>
 Mapper.xml 的模版名, 当需要覆盖默认的模版时使用该参数, 填写相对于 resources 的相对路径, 如 /template/mapper.txt
 
- <strong>include-services</strong>
  是否渲染 services, 默认 false
  
- <strong>services-template-name</strong>
  services 的模版名, 当 `include-services = true` 时属于必填参数, 填写相对于 resources 的相对路径, 如 /template/services.txt
  
- <strong>service-package</strong>
  Service 包名, 当 `include-services = true` 时属于必填参数, 如填写 ddd, 即 "${basic-package}.ddd"
  
- <strong>include-service-interfaces</strong>
  是否渲染 service 接口, 默认 false
  
- <strong>services-interface-template-name</strong>
  Service 接口的模版名, 当 `include-service-interfaces = true` 时属于必填参数, 填写相对于 resources 的相对路径, 如 /template/serviceInterface.txt
  
- <strong>services-interface-package</strong>
  Service 接口的包名, 当 `include-service-interfaces = true` 时属于必填参数, 如填写 ddd, 即 "${basic-package}.ddd"
  
- <strong>include-controller</strong>
  是否渲染 controller, 默认 false
  
- <strong>controller-template-name</strong>
  Controller 的模版名, 当 `include-controller = true` 时属于必填参数, 填写相对于 resources 的相对路径, 如 /template/controller.txt

- <strong>controller-package</strong>
  Controller 接口的包名, 当 `include-controller = true` 时属于必填参数, 如填写 ddd, 即 "${basic-package}.ddd"
  
- <strong>super-entity</strong>
  实体类的父类, 需要填写完整的类名( 即含有包名的全限定名 ), 并且该类能够在生成器所属的项目内找到
  
- <strong>super-services</strong>
  Service 的父类, 需要填写完整的类名( 即含有包名的全限定名 ), 并且该类能够在生成器所属的项目内找到
  
- <strong>super-repository</strong>
  Mapper 的父类, 需要填写完整的类名( 即含有包名的全限定名 ), 并且该类能够在生成器所属的项目内找到
  
- <strong>super-services</strong>
  Service 的父类, 需要填写完整的类名( 即含有包名的全限定名 ), 并且该类能够在生成器所属的项目内找到
  
- <strong>super-services-interface</strong>
  Service 接口的父类, 需要填写完整的类名( 即含有包名的全限定名 ), 并且该类能够在生成器所属的项目内找到
  
- <strong>super-controller</strong>
  Controller 的父类, 需要填写完整的类名( 即含有包名的全限定名 ), 并且该类能够在生成器所属的项目内找到
  
- <strong>convert-to-camel</strong>
  是否将下划线转为驼峰命名, 默认 true
  
- <strong>force-to-generate</strong>
  当文件存在时, 是否强制生成并覆盖原文件, 默认 false
  
- <strong>use-box-type</strong>
  对于基本类型是否使用包装类, 默认 false
  

### 参数配置说明

##### 路径

<strong>涉及到路径的所有参数, 均以 file-parent-path 属性作为起始路径, 也就是说配置上的路径均为相对路径</strong>

如 file-parent-path 地址为 `/Users/xxxx/idea/myProject`, 也就是说使用默认路径
那么我想在 maven 项目的 resources 里的 mapper 文件夹生成 xml 文件.

即配置 `resources-path: /src/main/java/resources/`, 最终在代码使用的路径是 `/Users/xxxx/idea/myProject/src/main/java/resources`

如果自定义了 file-parent-path, 值为 `/Users/xxxx/demo`, 那么最终代码使用的路径是 `/Users/xxxx/demo/src/main/java/resources`

##### 外置模版路径

thymeleaf 是使用 `ClassLoader.getResourceAsStream` 获取文件, 因此外部定义的模版需要放在 resources 文件夹内, 另外生成器使用 thymeleaf 的 TEXT 模式进行模版渲染, 如需要自定义模版需要学习 [Textual template modes](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#textual-template-modes)

注: 如需要在默认模版的基础上修改, 到 jar 包内( top.itfinally.mybatis-generator ) 的项目根目录上复制对应的模版进行修改。

##### 生成器在模版的内置对象

针对数据库的元数据进行处理时, 在设计上将单张表是为一个工作组, 并且根据不同的文件创建相对应的工作单元, 结构如下图:

```text
JobGroup ( 工作组, 对应一张数据表 )
   |
   | --- JobUnit( 实体 )
   | --- JobUnit( Mapper 类 )
   | --- JobUnit( Mapper.xml 文件 )
   | --- JobUnit( Service 类, 需要自行开启并定义模版 )
   | --- JobUnit( Service 的接口类, 需要自行开启并定义模版 )
   | --- JobUnit( Controller 类, 需要自行开启并定义模版 )
```

也就是说, 每个 JobUnit 对应一份文件, 而每个 JobGroup 对应一张数据表, 并且包含多个 JobUnit。

因此, 在模版的作用域上, 内置了四个元数据对象, 分别是:

 1. item - 即 JobUnit, 存放当前文件的元数据
 2. meta - 即 JobGroup, 存档所有文件的元数据
 3. properties - 即配置文件对象
 4. entity - 即当前 JobGroup 所对应的表的元数据

另外, 除 properties 以外( 这个配置其实就是上述的参数配置, 此处不在叙述 ), 其余三个实体的属性如下所示:

```text
JobGroup
   |
   | --- entity: JobUnit ( 实体元数据 )
   | --- services: JobUnit ( Service 元数据 )
   | --- mapperXml: JobUnit ( Mapper.xml 元数据 )
   | --- repository: JobUnit ( Mapper 元数据 )
   | --- controller: JobUnit ( Controller 元数据 )
   | --- servicesInterface: JobUnit ( Service 接口的元数据 )
   | --- tableEntity: TableEntity ( 对应数据表的元数据 )
   
JobUnit
   |
   | --- className: String ( 简写类名, 不包含包名 )
   | --- writePath: String ( 文件的绝对路径 )
   | --- packageName: String ( 包名 )
   | --- superClass: Class<?> ( 实体的父类对象 )
   
TableEntity
   |
   | --- jdbcName: String ( 数据表的名称 )
   | --- javaName: String ( 数据表在 Java 文件对应的基础名 )
   | --- comment: String ( 数据表的描述信息 )
   | --- columns: List<ColumnEntity> ( 数据表所有列的元数据 )
   | --- depends: List<String> ( 实体类需要 import 的所有依赖, 如 java.util.Date )
   | --- primaryKeys: List<ColumnEntity> ( 主键 )
   | --- uniqueKeys: List<UniqueKeyEntity> ( 唯一键 )
   | --- referenceKeys: List<ReferenceKeyEntity> ( 外键 )
   
ColumnEntity
   |
   | --- comment: String ( 数据列的描述 )
   | --- jdbcName: String ( 数据列的名称 )
   | --- javaName: String ( 数据列在 Java 文件对应的属性名 )
   | --- jdbcType: String ( 数据列在表内的类型 ) 
   | --- javaType: String ( 数据列在 Java 文件对应的类型 )
   | --- notNull: boolean ( 是否可空, true 则不可空 )
   | --- uniqueKey: boolean ( 是否唯一, true 即唯一 )
   | --- primaryKey: boolean ( 是否主键, true 即主键 )
   | --- javaTypeClass: Class<?> ( 该属性的类型的类对象 )
   | --- getterName: String ( 该属性的 getter 方法名称 )
   | --- setterName: String ( 该属性的 setter 方法名称 )
   | --- hidden: boolean ( 是否已在父类中存在, true 即已存在 )
   
UniqueKeyEntity
   |
   | --- keyName: String ( 唯一键在数据表内的名称 )
   | --- columns: List<ColumnEntity> ( 该唯一键的成员列 )
   
ReferenceKeyEntity
   |
   | --- keyName: String ( 外键在数据表内的名称 )
   | --- databaseName: String ( 外键字段所在的数据库的名称 )
   | --- tableName: TableEntity ( 外键字段所在的表 )
   | --- column: ColumnEntity ( 外键字段对应的列的元数据 )
   | --- referenceTable: TableEntity ( 外键关联的字段对应的表 )
   | --- referenceColumn: ColumnEntity ( 外键关联的字段的元数据 )
   | --- referenceDatabaseName: String ( 外键关联的字段所在的数据库的名称 )
```

另外外键无法在模版上表现出来，主要是无法通过代码判断该外键的关系到底是一对多, 多对一或者多对多? 因此在模版上直接省略对应的生成流程。

##### 数据表的名称转换

在默认情况下, 名称转换是根据数据表的名称将下划线命名转为驼峰命名, 若需要自行转换命名, 可实现 `io.github.itfinally.mybatis.generator.configuration.NamingMapping`, 并且在 spring 内注册该实现类, 如下:

```java
@Component
class MyNamingMapping implements NamingMapping {
  public String getMapping( String jdbcName ) {
    // your code
  }
}
```

然后生成器会根据该转换类的返回结果, 针对不同的类进行命名, 比如实体是 `NamingMapping.getMapping( ${jdbcName} ) + "Entity"`。

