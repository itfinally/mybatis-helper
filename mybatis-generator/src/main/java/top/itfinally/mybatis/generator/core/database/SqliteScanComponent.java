package top.itfinally.mybatis.generator.core.database;

import com.google.common.base.Joiner;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.springframework.stereotype.Component;
import top.itfinally.mybatis.generator.core.PrimitiveType;
import top.itfinally.mybatis.generator.core.database.entity.ColumnEntity;
import top.itfinally.mybatis.generator.core.database.entity.TableEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * All rights reserved.
 * Date: 2018/11/12
 * Author: itfinally
 */
@Component
public class SqliteScanComponent extends DatabaseScanComponent {
    @Override
    public List<TableEntity> getTables() {
        List<Map<String, String>> tables = metadataMapper.getTables();
        List<TableEntity> tableInfoList = new ArrayList<>( tables.size() );

        try {
            CreateTable table;
            TableEntity metadata;
            for ( Map<String, String> item : tables ) {
                table = ( CreateTable ) CCJSqlParserUtil.parse( item.get( "sql" ).toLowerCase() );
                metadata = extractColumnMetadata( extractTableMetadata( table ), table );
                tableInfoList.add( metadata );

                // Sqlite is not supported relationship analysis because there are no enough information
//                relationshipAnalyzing( metadata, table );
            }

        } catch ( JSQLParserException e ) {
            throw new IllegalStateException( "Failure to analysis table", e );
        }

        return tableInfoList;
    }

    private TableEntity extractTableMetadata( CreateTable table ) {
        return buildTableNameInJava( new TableEntity().setComment( "" )
                .setJdbcName( table.getTable().getName().toLowerCase() ) );
    }

    private TableEntity extractColumnMetadata( TableEntity metadata, CreateTable table ) {
        boolean isPrimary, isNonnull;
        Class<?> type, javaType;
        ColDataType dataType;
        ColumnEntity column;

        List<ColumnEntity> columnList = new ArrayList<>( table.getColumnDefinitions().size() );
        for ( ColumnDefinition item : table.getColumnDefinitions() ) {
            dataType = item.getColDataType();
            isPrimary = isPrimary( item.getColumnSpecStrings() );
            isNonnull = isPrimary || isNonnull( item.getColumnSpecStrings() );

            column = new ColumnEntity()
                    .setComment( "" )
                    .setNotNull( isNonnull )
                    .setPrimaryKey( isPrimary )
                    .setJdbcName( item.getColumnName().toLowerCase() )
                    .setJdbcType( dataType.getDataType().toUpperCase() )
                    .setJavaName( namingConverter.convert( ( item.getColumnName() ).toLowerCase(), true ) );

            javaType = getJavaType( column );
            type = properties.isUseBoxType() ? javaType : PrimitiveType.getType( javaType );

            buildGS( column.setJavaTypeClass( javaType ).setJavaType( null == type ? javaType.getSimpleName() : type.getSimpleName() ) );

            if ( column.isPrimaryKey() ) {
                metadata.addPrimaryKeys( column );
            }

            columnList.add( column );
        }

        metadata.setColumns( columnList );

        return metadata;
    }

    private boolean isNonnull( List<String> specStrings ) {
        return Joiner.on( " " ).join( specStrings ).toLowerCase().contains( "not null" );
    }

    private boolean isPrimary( List<String> specStrings ) {
        return Joiner.on( " " ).join( specStrings ).toLowerCase().contains( "primary key" );
    }
}
