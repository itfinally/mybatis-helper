package top.itfinally.mybatis.jpa.override;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.annotation.MethodResolver;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

import java.util.*;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/24       itfinally       首次创建
 * *********************************************
 *
 * 覆盖链路
 * MybatisProperties - Configuration - MapperRegistry - MapperProxyFactory - MapperProxy
 *
 * 在不手动提供 SqlSessionFactory 的情况下将使用 MybatisJpaConfiguration
 * 手动提供 SqlSessionFactory 的话需要重新设置 Configuration
 *
 * 默认忽略手动装配的 MybatisProperties 对象
 * </pre>
 */
@Primary
@org.springframework.context.annotation.Configuration
@ConfigurationProperties( prefix = "mybatis.configuration" )
public class MybatisJpaConfiguration extends Configuration {
    private final Configuration configuration;
    private final MapperRegistry mapperRegistry = new MybatisJpaMapperRegistry( this );

    public MybatisJpaConfiguration() {
        configuration = new Configuration();
    }

    public MybatisJpaConfiguration( Configuration configuration ) {
        this.configuration = configuration;
    }

    @Override
    public void addMappers( String packageName, Class<?> configurationType ) {
        mapperRegistry.addMappers( packageName, configurationType );
    }

    @Override
    public void addMappers( String packageName ) {
        mapperRegistry.addMappers( packageName );
    }

    @Override
    public <T> void addMapper( Class<T> type ) {
        mapperRegistry.addMapper( type );
    }

    @Override
    public <T> T getMapper( Class<T> type, SqlSession sqlSession ) {
        return mapperRegistry.getMapper( type, sqlSession );
    }

    // delegated method

    @Override
    public String getLogPrefix() {
        return configuration.getLogPrefix();
    }

    @Override
    public void setLogPrefix( String logPrefix ) {
        configuration.setLogPrefix( logPrefix );
    }

    @Override
    public Class<? extends Log> getLogImpl() {
        return configuration.getLogImpl();
    }

    @Override
    public void setLogImpl( Class<? extends Log> logImpl ) {
        configuration.setLogImpl( logImpl );
    }

    @Override
    public Class<? extends VFS> getVfsImpl() {
        return configuration.getVfsImpl();
    }

    @Override
    public void setVfsImpl( Class<? extends VFS> vfsImpl ) {
        configuration.setVfsImpl( vfsImpl );
    }

    @Override
    public boolean isCallSettersOnNulls() {
        return configuration.isCallSettersOnNulls();
    }

    @Override
    public void setCallSettersOnNulls( boolean callSettersOnNulls ) {
        configuration.setCallSettersOnNulls( callSettersOnNulls );
    }

    @Override
    public boolean isUseActualParamName() {
        return configuration.isUseActualParamName();
    }

    @Override
    public void setUseActualParamName( boolean useActualParamName ) {
        configuration.setUseActualParamName( useActualParamName );
    }

    @Override
    public boolean isReturnInstanceForEmptyRow() {
        return configuration.isReturnInstanceForEmptyRow();
    }

    @Override
    public void setReturnInstanceForEmptyRow( boolean returnEmptyInstance ) {
        configuration.setReturnInstanceForEmptyRow( returnEmptyInstance );
    }

    @Override
    public String getDatabaseId() {
        return configuration.getDatabaseId();
    }

    @Override
    public void setDatabaseId( String databaseId ) {
        configuration.setDatabaseId( databaseId );
    }

    @Override
    public Class<?> getConfigurationFactory() {
        return configuration.getConfigurationFactory();
    }

    @Override
    public void setConfigurationFactory( Class<?> configurationFactory ) {
        configuration.setConfigurationFactory( configurationFactory );
    }

    @Override
    public boolean isSafeResultHandlerEnabled() {
        return configuration.isSafeResultHandlerEnabled();
    }

    @Override
    public void setSafeResultHandlerEnabled( boolean safeResultHandlerEnabled ) {
        configuration.setSafeResultHandlerEnabled( safeResultHandlerEnabled );
    }

    @Override
    public boolean isSafeRowBoundsEnabled() {
        return configuration.isSafeRowBoundsEnabled();
    }

    @Override
    public void setSafeRowBoundsEnabled( boolean safeRowBoundsEnabled ) {
        configuration.setSafeRowBoundsEnabled( safeRowBoundsEnabled );
    }

    @Override
    public boolean isMapUnderscoreToCamelCase() {
        return configuration.isMapUnderscoreToCamelCase();
    }

    @Override
    public void setMapUnderscoreToCamelCase( boolean mapUnderscoreToCamelCase ) {
        configuration.setMapUnderscoreToCamelCase( mapUnderscoreToCamelCase );
    }

    @Override
    public void addLoadedResource( String resource ) {
        configuration.addLoadedResource( resource );
    }

    @Override
    public boolean isResourceLoaded( String resource ) {
        return configuration.isResourceLoaded( resource );
    }

    @Override
    public Environment getEnvironment() {
        return configuration.getEnvironment();
    }

    @Override
    public void setEnvironment( Environment environment ) {
        configuration.setEnvironment( environment );
    }

    @Override
    public AutoMappingBehavior getAutoMappingBehavior() {
        return configuration.getAutoMappingBehavior();
    }

    @Override
    public void setAutoMappingBehavior( AutoMappingBehavior autoMappingBehavior ) {
        configuration.setAutoMappingBehavior( autoMappingBehavior );
    }

    @Override
    public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior() {
        return configuration.getAutoMappingUnknownColumnBehavior();
    }

    @Override
    public void setAutoMappingUnknownColumnBehavior( AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior ) {
        configuration.setAutoMappingUnknownColumnBehavior( autoMappingUnknownColumnBehavior );
    }

    @Override
    public boolean isLazyLoadingEnabled() {
        return configuration.isLazyLoadingEnabled();
    }

    @Override
    public void setLazyLoadingEnabled( boolean lazyLoadingEnabled ) {
        configuration.setLazyLoadingEnabled( lazyLoadingEnabled );
    }

    @Override
    public ProxyFactory getProxyFactory() {
        return configuration.getProxyFactory();
    }

    @Override
    public void setProxyFactory( ProxyFactory proxyFactory ) {
        configuration.setProxyFactory( proxyFactory );
    }

    @Override
    public boolean isAggressiveLazyLoading() {
        return configuration.isAggressiveLazyLoading();
    }

    @Override
    public void setAggressiveLazyLoading( boolean aggressiveLazyLoading ) {
        configuration.setAggressiveLazyLoading( aggressiveLazyLoading );
    }

    @Override
    public boolean isMultipleResultSetsEnabled() {
        return configuration.isMultipleResultSetsEnabled();
    }

    @Override
    public void setMultipleResultSetsEnabled( boolean multipleResultSetsEnabled ) {
        configuration.setMultipleResultSetsEnabled( multipleResultSetsEnabled );
    }

    @Override
    public Set<String> getLazyLoadTriggerMethods() {
        return configuration.getLazyLoadTriggerMethods();
    }

    @Override
    public void setLazyLoadTriggerMethods( Set<String> lazyLoadTriggerMethods ) {
        configuration.setLazyLoadTriggerMethods( lazyLoadTriggerMethods );
    }

    @Override
    public boolean isUseGeneratedKeys() {
        return configuration.isUseGeneratedKeys();
    }

    @Override
    public void setUseGeneratedKeys( boolean useGeneratedKeys ) {
        configuration.setUseGeneratedKeys( useGeneratedKeys );
    }

    @Override
    public ExecutorType getDefaultExecutorType() {
        return configuration.getDefaultExecutorType();
    }

    @Override
    public void setDefaultExecutorType( ExecutorType defaultExecutorType ) {
        configuration.setDefaultExecutorType( defaultExecutorType );
    }

    @Override
    public boolean isCacheEnabled() {
        return configuration.isCacheEnabled();
    }

    @Override
    public void setCacheEnabled( boolean cacheEnabled ) {
        configuration.setCacheEnabled( cacheEnabled );
    }

    @Override
    public Integer getDefaultStatementTimeout() {
        return configuration.getDefaultStatementTimeout();
    }

    @Override
    public void setDefaultStatementTimeout( Integer defaultStatementTimeout ) {
        configuration.setDefaultStatementTimeout( defaultStatementTimeout );
    }

    @Override
    public Integer getDefaultFetchSize() {
        return configuration.getDefaultFetchSize();
    }

    @Override
    public void setDefaultFetchSize( Integer defaultFetchSize ) {
        configuration.setDefaultFetchSize( defaultFetchSize );
    }

    @Override
    public boolean isUseColumnLabel() {
        return configuration.isUseColumnLabel();
    }

    @Override
    public void setUseColumnLabel( boolean useColumnLabel ) {
        configuration.setUseColumnLabel( useColumnLabel );
    }

    @Override
    public LocalCacheScope getLocalCacheScope() {
        return configuration.getLocalCacheScope();
    }

    @Override
    public void setLocalCacheScope( LocalCacheScope localCacheScope ) {
        configuration.setLocalCacheScope( localCacheScope );
    }

    @Override
    public JdbcType getJdbcTypeForNull() {
        return configuration.getJdbcTypeForNull();
    }

    @Override
    public void setJdbcTypeForNull( JdbcType jdbcTypeForNull ) {
        configuration.setJdbcTypeForNull( jdbcTypeForNull );
    }

    @Override
    public Properties getVariables() {
        return configuration.getVariables();
    }

    @Override
    public void setVariables( Properties variables ) {
        configuration.setVariables( variables );
    }

    @Override
    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return configuration.getTypeHandlerRegistry();
    }

    @Override
    public void setDefaultEnumTypeHandler( Class<? extends TypeHandler> typeHandler ) {
        configuration.setDefaultEnumTypeHandler( typeHandler );
    }

    @Override
    public TypeAliasRegistry getTypeAliasRegistry() {
        return configuration.getTypeAliasRegistry();
    }

    @Override
    public MapperRegistry getMapperRegistry() {
        return configuration.getMapperRegistry();
    }

    @Override
    public ReflectorFactory getReflectorFactory() {
        return configuration.getReflectorFactory();
    }

    @Override
    public void setReflectorFactory( ReflectorFactory reflectorFactory ) {
        configuration.setReflectorFactory( reflectorFactory );
    }

    @Override
    public ObjectFactory getObjectFactory() {
        return configuration.getObjectFactory();
    }

    @Override
    public void setObjectFactory( ObjectFactory objectFactory ) {
        configuration.setObjectFactory( objectFactory );
    }

    @Override
    public ObjectWrapperFactory getObjectWrapperFactory() {
        return configuration.getObjectWrapperFactory();
    }

    @Override
    public void setObjectWrapperFactory( ObjectWrapperFactory objectWrapperFactory ) {
        configuration.setObjectWrapperFactory( objectWrapperFactory );
    }

    @Override
    public List<Interceptor> getInterceptors() {
        return configuration.getInterceptors();
    }

    @Override
    public LanguageDriverRegistry getLanguageRegistry() {
        return configuration.getLanguageRegistry();
    }

    @Override
    public void setDefaultScriptingLanguage( Class<?> driver ) {
        configuration.setDefaultScriptingLanguage( driver );
    }

    @Override
    public LanguageDriver getDefaultScriptingLanguageInstance() {
        return configuration.getDefaultScriptingLanguageInstance();
    }

    @Override
    public LanguageDriver getDefaultScriptingLanuageInstance() {
        return configuration.getDefaultScriptingLanuageInstance();
    }

    @Override
    public MetaObject newMetaObject( Object object ) {
        return configuration.newMetaObject( object );
    }

    @Override
    public ParameterHandler newParameterHandler( MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql ) {
        return configuration.newParameterHandler( mappedStatement, parameterObject, boundSql );
    }

    @Override
    public ResultSetHandler newResultSetHandler( Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql ) {
        return configuration.newResultSetHandler( executor, mappedStatement, rowBounds, parameterHandler, resultHandler, boundSql );
    }

    @Override
    public StatementHandler newStatementHandler( Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql ) {
        return configuration.newStatementHandler( executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql );
    }

    @Override
    public Executor newExecutor( Transaction transaction ) {
        return configuration.newExecutor( transaction );
    }

    @Override
    public Executor newExecutor( Transaction transaction, ExecutorType executorType ) {
        return configuration.newExecutor( transaction, executorType );
    }

    @Override
    public void addKeyGenerator( String id, KeyGenerator keyGenerator ) {
        configuration.addKeyGenerator( id, keyGenerator );
    }

    @Override
    public Collection<String> getKeyGeneratorNames() {
        return configuration.getKeyGeneratorNames();
    }

    @Override
    public Collection<KeyGenerator> getKeyGenerators() {
        return configuration.getKeyGenerators();
    }

    @Override
    public KeyGenerator getKeyGenerator( String id ) {
        return configuration.getKeyGenerator( id );
    }

    @Override
    public boolean hasKeyGenerator( String id ) {
        return configuration.hasKeyGenerator( id );
    }

    @Override
    public void addCache( Cache cache ) {
        configuration.addCache( cache );
    }

    @Override
    public Collection<String> getCacheNames() {
        return configuration.getCacheNames();
    }

    @Override
    public Collection<Cache> getCaches() {
        return configuration.getCaches();
    }

    @Override
    public Cache getCache( String id ) {
        return configuration.getCache( id );
    }

    @Override
    public boolean hasCache( String id ) {
        return configuration.hasCache( id );
    }

    @Override
    public void addResultMap( ResultMap rm ) {
        configuration.addResultMap( rm );
    }

    @Override
    public Collection<String> getResultMapNames() {
        return configuration.getResultMapNames();
    }

    @Override
    public Collection<ResultMap> getResultMaps() {
        return configuration.getResultMaps();
    }

    @Override
    public ResultMap getResultMap( String id ) {
        return configuration.getResultMap( id );
    }

    @Override
    public boolean hasResultMap( String id ) {
        return configuration.hasResultMap( id );
    }

    @Override
    public void addParameterMap( ParameterMap pm ) {
        configuration.addParameterMap( pm );
    }

    @Override
    public Collection<String> getParameterMapNames() {
        return configuration.getParameterMapNames();
    }

    @Override
    public Collection<ParameterMap> getParameterMaps() {
        return configuration.getParameterMaps();
    }

    @Override
    public ParameterMap getParameterMap( String id ) {
        return configuration.getParameterMap( id );
    }

    @Override
    public boolean hasParameterMap( String id ) {
        return configuration.hasParameterMap( id );
    }

    @Override
    public void addMappedStatement( MappedStatement ms ) {
        configuration.addMappedStatement( ms );
    }

    @Override
    public Collection<String> getMappedStatementNames() {
        return configuration.getMappedStatementNames();
    }

    @Override
    public Collection<MappedStatement> getMappedStatements() {
        return configuration.getMappedStatements();
    }

    @Override
    public Collection<XMLStatementBuilder> getIncompleteStatements() {
        return configuration.getIncompleteStatements();
    }

    @Override
    public void addIncompleteStatement( XMLStatementBuilder incompleteStatement ) {
        configuration.addIncompleteStatement( incompleteStatement );
    }

    @Override
    public Collection<CacheRefResolver> getIncompleteCacheRefs() {
        return configuration.getIncompleteCacheRefs();
    }

    @Override
    public void addIncompleteCacheRef( CacheRefResolver incompleteCacheRef ) {
        configuration.addIncompleteCacheRef( incompleteCacheRef );
    }

    @Override
    public Collection<ResultMapResolver> getIncompleteResultMaps() {
        return configuration.getIncompleteResultMaps();
    }

    @Override
    public void addIncompleteResultMap( ResultMapResolver resultMapResolver ) {
        configuration.addIncompleteResultMap( resultMapResolver );
    }

    @Override
    public void addIncompleteMethod( MethodResolver builder ) {
        configuration.addIncompleteMethod( builder );
    }

    @Override
    public Collection<MethodResolver> getIncompleteMethods() {
        return configuration.getIncompleteMethods();
    }

    @Override
    public MappedStatement getMappedStatement( String id ) {
        return configuration.getMappedStatement( id );
    }

    @Override
    public MappedStatement getMappedStatement( String id, boolean validateIncompleteStatements ) {
        return configuration.getMappedStatement( id, validateIncompleteStatements );
    }

    @Override
    public Map<String, XNode> getSqlFragments() {
        return configuration.getSqlFragments();
    }

    @Override
    public void addInterceptor( Interceptor interceptor ) {
        configuration.addInterceptor( interceptor );
    }

    @Override
    public boolean hasMapper( Class<?> type ) {
        return configuration.hasMapper( type );
    }

    @Override
    public boolean hasStatement( String statementName ) {
        return configuration.hasStatement( statementName );
    }

    @Override
    public boolean hasStatement( String statementName, boolean validateIncompleteStatements ) {
        return configuration.hasStatement( statementName, validateIncompleteStatements );
    }

    @Override
    public void addCacheRef( String namespace, String referencedNamespace ) {
        configuration.addCacheRef( namespace, referencedNamespace );
    }
}