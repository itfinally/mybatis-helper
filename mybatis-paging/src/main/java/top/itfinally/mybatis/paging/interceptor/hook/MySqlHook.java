package top.itfinally.mybatis.paging.interceptor.hook;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/14       itfinally       首次创建
 * *********************************************
 *
 * MySQL 不支持 with .. as .. , except, minus, intersect 等子句
 * 通过 SetOperationList 处理 MySQL 的 union 和 union all
 * </pre>
 */
public class MySqlHook implements SqlHook {
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private List<String> countingSql = new ArrayList<>();
    private String pagingSql;

    private MySqlHook( String originSql, long beginRow, long range ) throws JSQLParserException {
        final Limit limit = new Limit();
        limit.setRowCount( new LongValue( range ) );
        limit.setOffset( new LongValue( beginRow ) );

        logger.debug( "Ready to " );

        ( ( Select ) CCJSqlParserUtil.parse( originSql ) ).getSelectBody().accept( new SelectVisitor() {
            @Override
            public void visit( PlainSelect plainSelect ) {
                plainSelect.setLimit( limit );
                pagingSql = plainSelect.toString();

                plainSelect.setLimit( null );
                plainSelect.getSelectItems().clear();
                plainSelect.setSelectItems( createCountFunction() );

                countingSql.add( plainSelect.toString() );
            }

            @Override
            public void visit( SetOperationList setOpList ) {
                for ( SetOperation ops : setOpList.getOperations() ) {
                    if ( "union all".equalsIgnoreCase( ops.getASTNode().jjtGetValue().toString() ) ) {
                        List<SelectBody> selects = setOpList.getSelects();

                        for ( int index = ops.getASTNode().jjtGetNumChildren()  - 1; index >= 0; index -= 1 ) {
                            ( ( PlainSelect ) selects.get( index ) ).setUseBrackets( true );
                        }
                    }
                }

                setOpList.setLimit( limit );
                pagingSql = setOpList.toString();

                PlainSelect plainSelect;
                for ( SelectBody select : setOpList.getSelects() ) {
                    plainSelect = ( PlainSelect ) select;

                    plainSelect.setLimit( null );
                    plainSelect.setUseBrackets( false );
                    plainSelect.getSelectItems().clear();
                    plainSelect.setSelectItems( createCountFunction() );

                    countingSql.add( plainSelect.toString() );
                }
            }

            @Override
            public void visit( WithItem withItem ) {
                throw new UnsupportedOperationException( "MySQL doesn't supported WITH AS syntax" );
            }
        } );
    }

    @Override
    public String getPagingSql() {
        return pagingSql;
    }

    @Override
    public List<String> getCountingSql() {
        return Lists.newArrayList( countingSql );
    }

    private List<SelectItem> createCountFunction() {
        SelectExpressionItem selectExpression = new SelectExpressionItem();
        Function counter = new Function();

        counter.setName( "count" );
        counter.setAllColumns( true );

        selectExpression.setExpression( counter );
        return Lists.<SelectItem>newArrayList( selectExpression );
    }

    public static class Builder implements SqlHookBuilder {

        @Override
        public SqlHook build( String originSql, long beginRow, long range ) throws JSQLParserException {
            return new MySqlHook( originSql, beginRow, range );
        }
    }
}
