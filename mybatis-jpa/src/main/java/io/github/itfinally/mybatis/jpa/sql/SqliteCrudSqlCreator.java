package io.github.itfinally.mybatis.jpa.sql;

import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

public class SqliteCrudSqlCreator extends MysqlCrudSqlCreator {
  public SqliteCrudSqlCreator( Configuration configuration, XMLLanguageDriver languageDriver ) {
    super( configuration, languageDriver );
  }
}
