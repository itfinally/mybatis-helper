package io.github.itfinally.mybatis.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MybatisCoreConfiguration {
  public static final String MYSQL = "mysql";
  public static final String ORACLE = "oracle";
  public static final String SQLITE = "sqlite";

  private volatile String databaseId;

  @Resource
  private List<DataSource> dataSource;

  @Autowired( required = false )
  private AbstractRoutingDataSource routingDataSource;

  public DataSource getDatasource() {
    return routingDataSource != null ? routingDataSource : dataSource.get( 0 );
  }

  public Connection getConnection() {
    try {
      return getDatasource().getConnection();

    } catch ( SQLException e ) {
      throw new RuntimeException( "Failure to getting connection", e );
    }
  }

  public String getDatabaseId() {
    if ( null == databaseId ) {
      synchronized ( this ) {
        if ( null == databaseId ) {
          try ( Connection connection = getConnection() ) {
            databaseId = connection.getMetaData().getDatabaseProductName().toLowerCase();

          } catch ( SQLException e ) {
            throw new RuntimeException( "Cannot getting database name.", e );
          }
        }
      }
    }

    return databaseId;
  }
}
