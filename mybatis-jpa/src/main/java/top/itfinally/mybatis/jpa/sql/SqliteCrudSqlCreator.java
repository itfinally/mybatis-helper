package top.itfinally.mybatis.jpa.sql;

import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * All rights reserved.
 * Date: 2018/11/19
 * Author: itfinally
 */
public class SqliteCrudSqlCreator extends MysqlCrudSqlCreator {
    public SqliteCrudSqlCreator( Configuration configuration, XMLLanguageDriver languageDriver ) {
        super( configuration, languageDriver );
    }
}
