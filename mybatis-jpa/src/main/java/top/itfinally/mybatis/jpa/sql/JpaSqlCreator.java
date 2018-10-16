package top.itfinally.mybatis.jpa.sql;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import top.itfinally.mybatis.jpa.criteria.render.ParameterBus;
import top.itfinally.mybatis.jpa.mapper.BasicCriteriaQueryInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/10/11       itfinally       首次创建
 * *********************************************
 * </pre>
 */

public class JpaSqlCreator {
    private final XMLLanguageDriver languageDriver;
    private final Configuration configuration;

    public JpaSqlCreator( Configuration configuration, XMLLanguageDriver languageDriver ) {
        this.configuration = configuration;
        this.languageDriver = languageDriver;
    }

    public BoundSql buildSql( Map<String, Object> parameterBus ) {
        Map<String, Object> realParameters = new HashMap<>();
        String sql = ( String ) parameterBus.get( BasicCriteriaQueryInterface.SQL );

        for ( Map.Entry<String, Object> entry : parameterBus.entrySet() ) {
            if ( entry.getKey().startsWith( ParameterBus.PREFIX ) ) {
                realParameters.put( entry.getKey(), entry.getValue() );
            }
        }

        return languageDriver.createSqlSource( configuration, sql, Map.class ).getBoundSql( realParameters );
    }
}
