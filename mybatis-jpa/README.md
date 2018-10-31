# Mybatis-JPA

Mybatis-Jpa 是基于 Mybatis 的对象查询插件, 主要面向但不限于下列场景:

- <strong>简单或中等复杂程度的 SQL 语句编写</strong>

  如 `select count(*) from demo where field = ?` 这类简单语句, 在实际开发中这类查询语句在项目内占比极高, 而且条件不尽相同, 每个语句重用程度比较低, 这种场景下对象查询可以简化工作量.

- <strong>SQL 延迟执行</strong>

  如果需要在某种环境下创建并固化 SQL, 然后到某个合适的环境再执行, 比如在某些特定的事务内, 那对象查询也能解决这种需求.
  
- <strong>懒惰</strong>

  如果单纯因为懒得写 Mapper 的模版, 对象查询专治老司机各种不服.

除了提供对象查询外, 该插件亦提供通用 Crud 接口, 无需编写 SQL 即可执行相应的操作. 当然也因为该插件是通过拦截器内嵌, 进而动态改变执行 SQL 的元数据, 因此无缝对接 Mybatis 的属性映射, 类型转换, 防SQL注入, 事务等基础设施.

该插件参考并采用了 Hibernate5 Criteria 的设计, 从而使整体设计更加简洁, 更语义化. 并且能够支持简单及中等复杂程度的 SQL 语句, 包括查询 / 更新 / 删除三种动作. 在一定程度上, 该插件也可以看作 Hibernate5 Criteria Query 的移植版.

当然, 由于是建立在 Mybatis 之上, 大部分工作都交给原流程, 因而整体上仅仅只多了一层模版翻译过程, 实际性能与 Mybatis 相差不大.

由于通用接口属于定制化功能, 需要根据不同的数据库对 SQL 进行优化, 因此目前仅支持 MySQL, 并且在 Java 版本上该插件仅支持 Java7/8 两个版本, 未来将会直接支持 Java11 及以上版本.

<strong>注意</strong>: 这里如此强调是 Hibernate5 而不是 Hibernate, 是因为 Hibernate5 的 Criteria 整个架构完全推翻过去的设计, 更加简洁和语义化, 如有疑惑请移步[Criteria Query 设计文档](https://github.com/itfinally/mybatis-helper/blob/master/mybatis-jpa/Criteria%20Query%20%E8%AE%BE%E8%AE%A1%E6%96%87%E6%A1%A3.md)查看其中的设计, 如果有使用过 Hibernate4/5 两个版本的同学应该能明显感觉到两个版本的差异.

### 添加 maven 依赖( Java7/8 版本 )
```xml
<dependency>
  <groupId>top.itfinally</groupId>
  <artifactId>mybatis-jpa-spring-boot-starter</artifactId>
  <version>1.0.0.RELEASE</version>
</dependency>
```

### 配置及使用方式

该插件也依赖实体上的 JPA 注解, 如 `@Table`, `@Column`, `@OneToOne` 等注解, 要按照相关规范为实体标记.

注: 如果没有 `@Column` 或 `@JoinColumn` 修饰, 那么该属性会被忽略, 另外被修饰的属性必须有 getter/setter 方法.

```java
@Table( name = "demo" )
public class DemoEntity {

  @Id
  @Column( name = "id" )
  private String id;
  
  // etc...
}
```

然后需要为该插件配置实体的路径, 使用对象配置或者 yml 文件配置均可. 
注意, <strong>多个包路径用英文逗号分隔</strong>.

yml 配置如下:
```yml
mybatis:
  jpa:
    entity-scan: ${Your entity package path}
```

对象配置如下:
```java
@Bean
@Primary
public MybatisJpaConfigureProperties property() {
  return new MybatisJpaConfigureProperties().setEntityScan( "Your entity package path" )
}
```

其次需要自己构建 `SqlSessionFactory`, 必须在这里将 `top.itfinally.mybatis.jpa.override.MybatisConfiguration` 配置类手动诸如 Mybatis 的工厂类, 否则 Mybatis 的内部实现将使用自己的 Configuration.

```java
@Bean
public SqlSessionFactory sqlSessionFactory( Configuration configuration, DataSource dataSource ) throws Exception {
  SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
  factory.setDataSource( dataSource );
  factory.setConfiguration( configuration );
  
  // Other setting...
  
  return factory.getObject();
}
```

<strong>注意</strong>: 这里必须用 Spring 注入的 Configuration, 当然由于 JPA 插件的 @Primary 注解, 这里真正注入的是 MybatisConfiguration 类. 只有使用 Spring 注入的配置, 那么该对象才会带有诸如 property / yml 等配置文件上的配置属性, 如果使用自己 new 出来的对象, 那么将无法让 Spring 注入配置属性而导致配置文件上所有配置失效.

最后 Mybatis 接口需要继承 `top.itfinally.mybatis.jpa.mapper.BasicCrudMapper<Entity>`, 该接口需要给出对应的实体, 比如 DemoMapper 负责 DemoEntity 实体的相关操作. 那么相应地, 需要在类型参数上指定该实体, 否则在使用该插件时会报错.

```java
@Mapper
interface DemoMapper extends BasicCrudMapper<DemoEntity> {
}
```

然后就可以通过 spring 注入的对象执行相应的数据库操作了.


#### 通用查询

通用查询主要包含以下 API( 注：<strong>其中的 Entity 为泛型变量</strong> )

```java
Entity queryByIdIs( String id );

List<Entity> queryByIdIn( List<String> ids );

List<Entity> queryAll();

boolean existByIdIs( String id );

int save( Entity entity );

int saveWithNonnull( Entity entity );

int saveAll( List<Entity> entities );

int updateByIdIs( Entity entity );

int updateWithNonnullByIdIs( Entity entity );

int deleteByIdIs( String id );

int deleteAllByIdIn( List<String> ids );
```

以上 API 开箱即用, 其中 id 为 String 类型, 但实际项目中会存在 string 类型的 uuid, 也有 int 类型的自增长 id, 这里统一使用字符串表达.

#### JPA Criteria 查询

由于 API 设计几乎与 Hibernate5 保持一致, 因此如果有 Hibernate5 Criteria 使用经验的同学应该是非常容易上手.

如果要使用对象查询, 在 `BasicCrudMapper` 接口内还有一个 API:
```java
CriteriaQueryManager<Entity> getCriteriaQueryManager();
```

该接口将会返回一个 Manager 实例, 通过该实例, 我们可以构建出几乎各种各样的 SQL. 但首先需要通过这个 Manager 实例构建 Builder 实例, 操作如下:

```java
// 第一步
CriteriaQueryManager<DemoEntity> manager = demoMapper.getCriteriaQueryManager();

// 第二步
CriteriaBuilder builder = manager.getCriteriaBuilder();

// 第三步
CriteriaQuery<DemoEntity> query = builder.createCriteriaQuery();
```

看似繁琐, 实际上三个实例各司其职.
`CriteriaQueryManager` 实例用于创建 Builder 及执行 SQL 的职责.<br/>
`CriteriaBuilder` 实例用于构建各种各样的 SQL 子句.<br/>
`CriteriaQuery` 实例用于关联所有 SQL 子句, 代表最终执行的 SQL, 并最终作为参数传递给 `CriteriaQueryManager` 执行.

##### 简单查询
让我们先用一个简单的例子做示范.

```java
Root<DemoEntity> root = query.from( DemoEntity.class );
query.select( root );
manager.createQuery( query ).getSingleResult();
```

以上就是一个简单的数据查询, 前两行代码代表着语句 `select * from demo`, 第三行则是执行并获取单个数据. 在这里, <strong>表与字段的结构用树结构进行表达, 表就是根节点, 而字段就是子节点</strong>.

```text
          table
    /       |       \
  field1  field2  field3
```

我们来写个稍微复杂一点的 SQL.

```java
Root<DemoEntity> root = query.from( DemoEntity.class );
query.select( root.get( "name" ) )
     .where( builder.equal( root.get( "id" ), 123 ) );
manager.createQuery( query ).getSingleResult();
```

以上代码相当于语句 `select demo.name from demo demo where demo.id = 123`, 可以看到其中的条件是用过 builder 实例进行构建的. 实际上, <strong>所有的条件都是通过 builder 实例进行构建</strong>.

留意 SQL 语句, 会发现这个表使用了别名, 而且在这个简单语句内别名似乎是多此一举。但是在设计上, 是没办法判断使用者是否写出了一个复杂的 SQL 语句, 因此统一使用别名是一种避免名称冲突的最保险的方式.

<strong>如果需要修改别名, 可以使用 `alias` 方法, 所有表达式均可使用该方法设置别名.</strong>

注意, 这里的 get 方法内填写的是实体的 java 属性名称, 而不是对应数据表的字段名, 插件会根据实体上的 JPA 注解映射到对应的数据表字段.

继续写个复杂的 SQL 作为事例.

```java
Root<DemoEntity> root = ( Root<DemoEntity> ) query.from( DemoEntity.class ).alias( "demo2" );

query.select( builder.count( root ).alias( "total" ) )
     .where( builder.or(
       builder.like( root.get( "name" ), "%jack%" ),
       builder.isFalse( root.get( "close" ) )
     ) );
manager.createQuery( query ).as( int.class ).getSingleResult();
```

以上代码相当于语句 `select count(*) as total from demo demo2 where demo2.name like '%jack%' or demo2.close is false`, 虽然 SQL 已经复杂了不少, 但是这套 Criteria Query 依然能够较为语义化地表达出 SQL 的含义.

这里用到 `builder.or` 表示逻辑或操作, <strong>在默认情况下, where 操作使用的是逻辑与操作</strong>.

注意最后一行代码的 `as( int.class )`, 该操作可以改变 manager 执行查询后所返回的值的类型. <strong>在默认情况下, manager 由哪个 mapper 创建出来的, 那么查询返回的类型就是对应 mapper 内泛型参数指定的类型</strong>.

最后我们再来一个更为复杂的查询.

```
Root<A> rootA = query.from( A.class );
Root<B> rootB = query.from( B.class );
Root<C> rootC = query.from( C.class );

SubQuery<D> subQuery = query.subQuery();
Root<D> rootD = subQuery.from( D.class );

rootB.join( "bId", JoinType.LEFT ).on( builder.equal( rootA.get( "bId" ), rootB.get( "id" ) ) );

query.select( rootA, rootC ).where( 

          rootA.get( "name" ).in( subQuery.select( rootD.get( "nickname" ) ) ),
          builder.greaterThanOrEqualTo( root.get( "createTime" ), "2018-10-24" )
          
    ).orderBy( builder.desc( rootA.get( "createTime" ) ) )
```

以上代码相当于语句 `select a.* from a a left join b b, c c on a.b_id = b.id where a.name in ( select d.nickname from d d ) and a.create_time >= '2018-10-24' order by a.create_time desc`.

这里先看 C 表, 这张表由于没有关联任何表, 但的确是通过 query 对象创建的, 因此也会加入到查询的表内.

然后是 D 表, 这里 D 表是一个子查询, 子查询的操作实际上与普通查询是一样的. 注意, 子查询必须是基于一张数据库内存在的物理表创建, 而无法基于一张中间结果表进行创建, 因为这种语句使用频率低, 而且加入到对象查询的成本过高, 可能会导致整体设计变的复杂而难懂, 因此这种复杂程度的语句最好是直接写 SQL 语句. 

##### 关联查询

该插件也支持关联查询, 使用的同样是 JPA 的方式, 看下列例子.

```java
@Table( name = "demo" )
public class DemoEntity {

  @Column( name = "field" )
  public String field;
  
  @OneToOne
  @JoinColumn( name = "demo2_id", referencedColumnName = "id" )
  public Demo2Entity demo2Id;
  
  @OneToOne( fetch = FetchType.LAZY )
  @JoinColumn( name = "demo2_id", table = "demo2", referencedColumnName = "id" )
  public Map<String, Object> demo2Id;
  
  @OneToMany( targetEntity = Demo3Entity.class )
  @JoinColumn( name = "demo3_id", referencedColumnName = "id" )
  public List<Map<String, Object>> demo3Entities;
  
  @ManyToOne
  @JoinColumn( name = "demo4", referencedColumnName = "demo_id" )
  public Demo4Entity demo4;
}
```

这里看到如 `@OneToOne` 之类的关系描述注解是与 `@JoinColumn` 一起使用, 一般地, 需要填写的信息会根据属性的 Java 类型不同而不同, <strong>但 name / referencedColumnName 属性属于必填字段</strong>.

其余属性则是视其 Java 类型而定.

- 如果类型是某个实体, 那么无需再添加更多信息, 插件会根据该实体的类对象找出相应的描述信息.
- 如果类型是 Map 及其子类, 那么需要在 JoinColumn 指定 table 属性, 或者在对应的关系描述注解指定 targetEntity 属性.
- 如果类型是 Collection 及其子类, 则根据该类的泛型参数的类型作为判断类型, 根据上述两点进行判断.

如果类型是 Map 及其子类, 如果查询出的列名与实体上的注解匹配, 那么 key 是实体内的属性名, 否则跟随别名.

如 `select field_alias, other_field from ...` 这个语句.

以 'field_alias' 为例, 如果对应的实体内存在一个被 `@Column( name = 'field_alias' )` 等标签修饰并且名为 'testField' 的实体属性, 那么 Map 内的 key 就是 'testField'.

以 'other_field' 为例, 如果实体内没有符合以上条件的属性, 那么 Map 内的 key 就是 'other_field'.

另外, 关联查询支持懒加载, 即使是 Map 亦可, 只需要将 fetch 属性声明为 `FetchType.LAZY` 即可.

针对多对多这种关系, 由于这种关系是可以分解的, 并且考虑到程序分析其关系并且创造出关联查询 SQL 是不现实的, 因此插件不支持 `@ManyToMany` 的修饰, 建议分解成一对多的关系进行解决.

对于关联操作的 cascade 属性, 目前插件暂不支持, 后续会考虑加入该功能.

