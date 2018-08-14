package top.itfinally.mybatis.paging.hook;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.SelectUtils;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/14       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class MySqlHook implements SqlHook {
    private final String pagingSql;
    private final String countingSql;

    MySqlHook( String originSql, long beginRow, long range ) throws JSQLParserException {
        Select select = ( Select ) CCJSqlParserUtil.parse( originSql );
        PlainSelect selectBody = ( PlainSelect ) select.getSelectBody();

        Limit limit = new Limit();
        limit.setOffset( new LongValue( beginRow ) );
        limit.setRowCount( new LongValue( range ) );

        selectBody.setLimit( limit );

        pagingSql = select.toString();

        selectBody.setLimit( null );

        SelectExpressionItem selectExpression = new SelectExpressionItem();
        Function counter = new Function();

        counter.setName( "count" );
        counter.setAllColumns( true );

        selectExpression.setExpression( counter );

        selectBody.setSelectItems( Lists.<SelectItem>newArrayList( selectExpression ) );

        countingSql = select.toString();
    }

    @Override
    public String getPagingSql() {
        return pagingSql;
    }

    @Override
    public String getCountingSql() {
        return countingSql;
    }
}
