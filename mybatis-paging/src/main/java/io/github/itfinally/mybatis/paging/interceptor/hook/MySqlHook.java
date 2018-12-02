package io.github.itfinally.mybatis.paging.interceptor.hook;

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

// MySQL 不支持 with .. as .. , except, minus, intersect 等子句
// 通过 SetOperationList 处理 MySQL 的 union 和 union all
public class MySqlHook implements SqlHook {
  private final Logger logger = LoggerFactory.getLogger( getClass() );

  private List<String> countingSql = new ArrayList<>();
  private String pagingSql;

  private MySqlHook( String originSql, long beginRow, long range ) throws JSQLParserException {
    final Limit limit = new Limit();
    limit.setRowCount( new LongValue( range ) );
    limit.setOffset( new LongValue( beginRow ) );

    logger.debug( "Ready to build paging and counting sql." );

    ( ( Select ) CCJSqlParserUtil.parse( originSql ) ).getSelectBody().accept( new SelectVisitor() {
      @Override
      public void visit( PlainSelect plainSelect ) {
        plainSelect.setLimit( limit );
        pagingSql = plainSelect.toString();

        logger.debug( "Build paging sql: {}", pagingSql );

        plainSelect.setLimit( null );
        plainSelect.getSelectItems().clear();
        plainSelect.setSelectItems( createCountFunction() );

        countingSql.add( plainSelect.toString() );

        logger.debug( "Build counting sql: {}", countingSql );
      }

      @Override
      public void visit( SetOperationList setOpList ) {
        List<SelectBody> selects;
        int index;

        for ( SetOperation ops : setOpList.getOperations() ) {
          if ( "union all".equalsIgnoreCase( ops.getASTNode().jjtGetValue().toString() ) ) {
            selects = setOpList.getSelects();

            for ( index = ops.getASTNode().jjtGetNumChildren() - 1; index >= 0; index -= 1 ) {
              ( ( PlainSelect ) selects.get( index ) ).setUseBrackets( true );
            }
          }
        }

        setOpList.setLimit( limit );
        pagingSql = setOpList.toString();

        logger.debug( "Build paging sql: {}", pagingSql );

        PlainSelect plainSelect;
        for ( SelectBody select : setOpList.getSelects() ) {
          plainSelect = ( PlainSelect ) select;

          plainSelect.setLimit( null );
          plainSelect.setUseBrackets( false );
          plainSelect.getSelectItems().clear();
          plainSelect.setSelectItems( createCountFunction() );

          countingSql.add( plainSelect.toString() );
        }

        logger.debug( "Build counting sql: {}", countingSql );
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
