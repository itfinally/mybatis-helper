# Mybatis-Helper 

该项目是 Mybatis 插件包, 用于扩展 Mybatis 的特性, 其中包含 生成器( generator ) / 分页插件( paging ) / JPA ( 对象查询 ).

该项目基于 Java7, Mybatis3.4.6 开发, 并且使用 HikariCP-java7 作为连接池, log4j2 作为日志系统, 如若需要使用其他组件, 请自行定义 exclusion.

此处是各组件均使用的配置或操作, 组件详细的文档请查阅各自的文件夹.

### 多数据源配置

如果需要各组件支持多数据源, 首先需要自行定义多个 `DataSource`

```java
@Bean( "masterDataSource" )
public DataSource() {
  // maybe master dataSource?
}

@Bean( "replicaDataSource" )
public DataSource() {
  // maybe replica dataSource?
}
```

然后初始化 `DynamicDataSourceRouter`, 这是动态数据源路由, 继承了 spring 提供的 `AbstractRoutingDataSource`, 而该类又间接实现了 `DataSource`, 因此该类可以直接作为数据源使用.

```java
@Primary
@Bean( "dataSourceRouter" )
public DataSource( ApplicationContext context ) {
  DynamicDataSourceRouter dataSourceRouter = new DynamicDataSourceRouter( context );
  dataSourceRouter.setDefaultTargetDataSource( "dataSourceMaster" );
  
  Map<Object, Object> dataSourceMap = new HashMap<>();
  
  // key 是数据源的别名, 可以通过该别名动态转变使用的数据源
  // value 是真实数据源实例在 spring 的别名, 当然 value 也可以是 DataSource 实例
  dataSourceMap.put( "master", "dataSourceMaster" );
  dataSourceMap.put( "replica", "dataSourceReplica" );
  dataSourceRouter.setTargetDataSources( dataSourceMap );
  
  return dataSourceRouter;
}
```

首先注意这里是用了 `@Primary` 标记该数据源路由, 后续所有使用数据源均使用该路由, 而路由本身是通过抽象方法 `determineCurrentLookupKey()` 获取当前使用的数据源的 key, 也就是上面变量 dataSourceMap 所配置的 key.

当需要改变数据源时, 通过 `DynamicDataSourceRouter.setDataSourceName( String name )` 给出数据源的别名, 即可改变后续操作使用的数据源.

当然这种做法虽然是最简单的, 但是可能无法在同一个函数作用域内使用不同的数据源. 除非使用 AOP 拦截 Mapper 并强制使用JDK代理, 但这样亦会导致无法在同一事务内操作, 因为各个 Mapper 的数据库连接都是不相同的.

因此对于主从分离这类场景, 最好还是使用诸如 sharding-jdbc 等第三方解决方案.



