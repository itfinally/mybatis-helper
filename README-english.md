# Mybatis-Helper

This is a general configuration doc, and detail of other components is in there are own folder.

### multi-dataSource

If want to use multi-dataSource, You should  prepare more than one dataSource in first.

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

And initialize class `DynamicDataSourceRouter`, this is dynamic dataSource route and extend class `AbstractRoutingDataSource` that spring offer.

```java
@Primary
@Bean( "dataSourceRouter" )
public DataSource( ApplicationContext context ) {
  DynamicDataSourceRouter dataSourceRouter = new DynamicDataSourceRouter( context );
  dataSourceRouter.setDefaultTargetDataSource( "dataSourceMaster" );
  
  Map<Object, Object> dataSourceMap = new HashMap<>();
  
  // key is data source alias, it allow you change data source in dynamic
  // value is data source alias in spring, also value can be an instance of data source
  dataSourceMap.put( "master", "dataSourceMaster" );
  dataSourceMap.put( "replica", "dataSourceReplica" );
  dataSourceRouter.setTargetDataSources( dataSourceMap );
  
  return dataSourceRouter;
}
```

Let us focus on `@Primary` annotation, This is a proxy of data source, and when you config other components who depend data source, should be use proxy but no the real data source.

When spring use proxy data source, it will call abstract method `determineCurrentLookupKey()` to get the key of current data source that you want to use. And this key is the hash map key of you setting on `dataSourceMap`.

Then you can use `DynamicDataSourceRouter.setDataSourceName( String name )` to give a name that lets spring change data source.

This solution is an easy way, but it have a problem, you can't use different data source in the same method. 

If you try to force to use jdk proxy to intercepte all Mybatis's mapper, then it just put you into other problem, you can't commit all SQL in the same transaction because all mapper using different database connection.

So if you database is use some kind of master-replicated mode, you better to use solution like sharding-jdbc to resolve it.

