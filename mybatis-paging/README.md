# mybatis-paging

Mybatis-Paging 是一款为 Mybatis 而设计的分页插件, 该插件小巧轻便, 不使用反射进行修改参数, 对程序损耗极小。

<strong>目前仅支持 MySQL 数据库, 并且在 MyBatis 3.4.6 下进行开发。</strong>


### 添加 maven 依赖
```xml
<dependency>
  <groupId>io.github.itfinally</groupId>
  <artifactId>mybatis-paging-spring-boot-starter</artifactId>
  <version>1.0.0.RELEASE</version>
</dependency>
```


### 使用方式

只需要在查询前使用 `Pager` 对象给出分页参数即可, 后续的第一次查询即为分页查询。

另外如果需要获取当前分页的总页数和总数据量, 可以使用 `PagingUtil` 进行辅助查询。

在大多数情况下, `Pager` 是不需要释放的, 如果需要连续对 sql 进行分页, 可以在给出分页参数的同时设置第三个参数 `holding`, 后续所有 sql 都会加入分页流程。

因分页参数是通过 ThreadLocal 传递, 因此当 `holding` 设置为 true 后, 当不再需要分页时, 必须手动调用 `Pager.clear()` 进行清理, 或者通过 try-with-resources 进行清理。

```
try ( Pager pager = Pager.getInstance() ) {
  // your code
}
```

注: 当发生异常时, 除 main 线程以外( main 线程异常等同于程序退出, 并且该线程是由 jvm 管理 ), 其余线程均会自动清理 ThreadLocal 的参数, 因此不需要关心该参数是否会影响下次查询。( 即使没有使用 try-with-resources 亦是如此 )


### 配置

默认情况下是不需要配置的, 但也有例外情况。需要配置时使用 `top.itfinally.mybatis.paging.configuration.MybatisPagingProperties`, 并且如下设置:

```java
@Bean
public MybatisPagingProperties property() {
  return new MybatisPagingProperties();
}
```

##### 处理起始索引

在日常开发中, 常常会出现以 1 为起始索引, 以符合用户的使用习惯, 而程序往往以 0 为起始索引, 这个问题说大不大, 说小不小。

在分页插件中, 默认状态下起始索引为 0, 可以通过设置 `MybatisPagingProperties.setIndexStartingWith` 改变起始索引。

比如某张表的数据以 10 作为单页大小, 可以分为 10 页, 此时以 1 作为起始索引:

```java
Pager.pagingAsPage( 0, 10 ); //  throws IllegalArgumentException, must be 0 > ${indexStartingWith}

Pager.pagingAsPage( 1, 10 );
List<Entity> entities = mapper.selectList(); // limit 0, 10

PageUtil.getTotalPage( entities );  // 10
PageUtil.getTotalCount( entities ); // 100

Pager.pagingAsPage( 2, 10 );
List<Entity> entities = mapper.selectList(); // limit 10, 20

Pager.pagingAsPage( 10, 10 );
List<Entity> entities = mapper.selectList(); // limit 90, 100
```

注: 在 `Pager` 给出的页码不能小于起始索引。

##### 自定义分页Sql

其中, 如果需要自行设置分页Sql, 可以继承 `top.itfinally.mybatis.paging.interceptor.hook.SqlHook` 并且通过`MybatisPagingProperties.addSqlHook( String databaseId, SqlHookBuilder sqlHookBuilder )` 编写自己的 hook( 钩子 ) 实现, 分页插件会优先检查外部提供的 Hook, 只有没对应的外部提供的 Hook 时才会调用默认的实现。

注: 上述方法的 databaseId 是指 jdbc 通过 `connection.getMetaData().getDatabaseProductName().toLowerCase()` 所返回的字符串, 不区分大小写。

另外编写 Hook 时需要实现 `top.itfinally.mybatis.paging.interceptor.hook.SqlHook` 接口。

```java
public interface SqlHook {
  // 返回分页 Sql
  String getPagingSql();
  
  // 返回执行计算数据总数的 Sql
  List<String> getCountingSql();
}
```

其次实现 `top.itfinally.mybatis.paging.interceptor.hook.SqlHookBuilder` 接口以返回自定义的 Hook。

```java
public interface SqlHookBuilder {
    SqlHook build( String originSql, long beginRow, long range ) throws JSQLParserException;
}
```

留意 `List<String> getCountingSql()`, 这里返回多个统计用的 Sql, 是因为 Sql 语句有可能出现 except, minus, intersect, union, union all 等多个子句合并的 Sql 语句, 在自己的 Hook 实现内可以根据不同的集合运算语义进行 Sql 拆分并执行数据统计, 最后再进行汇总, 分页插件使用这种设计尽可能将附带 Sql 对程序执行时间的影响降至最低。

