package top.itfinally.mybatis.generator.core.database;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;
import top.itfinally.mybatis.generator.core.PrimitiveType;
import top.itfinally.mybatis.generator.core.database.entity.ColumnEntity;
import top.itfinally.mybatis.generator.core.database.entity.ReferenceKeyEntity;
import top.itfinally.mybatis.generator.core.database.entity.TableEntity;
import top.itfinally.mybatis.generator.core.database.entity.UniqueKeyEntity;

import java.util.*;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/30       itfinally       首次创建
 * *********************************************
 * </pre>
 */

@Component
public class MysqlScanComponent extends DatabaseScanComponent {

    @Override
    public List<TableEntity> getTables() {
        List<Map<String, String>> tables = metadataMapper.getTables();
        Map<String, TableEntity> tableMapping = new HashMap<>( tables.size() );
        List<TableEntity> tableInfoList = new ArrayList<>( tables.size() );

        TableEntity table;
        for ( Map<String, String> item : tables ) {
            table = extractColumnMetadata( extractTableMetadata( item ) );
            tableMapping.put( table.getJdbcName(), table );
            tableInfoList.add( table );
        }

        for ( TableEntity item : tableInfoList ) {
            relationshipAnalyzing( item, tableMapping );
        }

        return tableInfoList;
    }

    private TableEntity extractTableMetadata( Map<String, String> tableMetadata ) {
        return buildTableNameInJava( new TableEntity()
                .setJdbcName( ( tableMetadata.get( "TABLE_NAME" ) ).toLowerCase() )
                .setComment( Strings.isNullOrEmpty( tableMetadata.get( "TABLE_COMMENT" ) )

                        ? "" : ( tableMetadata.get( "TABLE_COMMENT" ) ).toLowerCase() ) );
    }

    private TableEntity extractColumnMetadata( TableEntity table ) {
        List<Map<String, String>> columns = metadataMapper.getColumns( table.getJdbcName() );
        List<ColumnEntity> columnList = new ArrayList<>( columns.size() );
        ColumnEntity column;
        Class<?> type;

        Class<?> javaType;
        for ( Map<String, String> item : columns ) {
            column = new ColumnEntity()
                    .setComment( item.get( "COLUMN_COMMENT" ) )
                    .setJdbcType( item.get( "DATA_TYPE" ).toUpperCase() )
                    .setJdbcName( item.get( "COLUMN_NAME" ).toLowerCase() )
                    .setNotNull( "no".equalsIgnoreCase( item.get( "IS_NULLABLE" ) ) )
                    .setPrimaryKey( "pri".equalsIgnoreCase( item.get( "COLUMN_KEY" ) ) )
                    .setJavaName( namingConverter.convert( ( item.get( "COLUMN_NAME" ) ).toLowerCase(), true ) );

            javaType = getJavaType( column );
            type = properties.isUseBoxType() ? javaType : PrimitiveType.getType( javaType );

            buildGS( column.setJavaTypeClass( javaType ).setJavaType( null == type ? javaType.getSimpleName() : type.getSimpleName() ) );

            if ( column.isPrimaryKey() ) {
                table.addPrimaryKeys( column );
            }

            columnList.add( column );
        }

        return table.setColumns( columnList );
    }

    private void relationshipAnalyzing( TableEntity table, Map<String, TableEntity> tableMapping ) {
        List<Map<String, String>> tableKeys = metadataMapper.getTableKeys( table.getJdbcName() );

        ColumnEntity column;
        Set<String> keyNames;
        ColumnEntity[] columns;
        List<String> indexList;
        ColumnEntity referenceColumn;

        for ( Map<String, String> item : tableKeys ) {
            if ( "unique".equalsIgnoreCase( item.get( "constraint_type" ) ) ) {
                indexList = Lists.newArrayList( item.get( "columns" ).toLowerCase().split( "," ) );
                columns = new ColumnEntity[ indexList.size() ];
                keyNames = Sets.newHashSet( indexList );

                for ( ColumnEntity subItem : table.getColumns() ) {
                    if ( keyNames.contains( subItem.getJdbcName() ) ) {
                        columns[ indexList.indexOf( subItem.getJdbcName() ) ] = subItem;
                    }
                }

                table.addUniqueKeys( new UniqueKeyEntity()
                        .setKeyName( item.get( "constraint_name" ) )
                        .setColumns( Lists.newArrayList( columns ) ) );
            }

            if ( "foreign key".equalsIgnoreCase( item.get( "constraint_type" ) ) ) {
                keyNames = Sets.newHashSet( item.get( "columns" ).toLowerCase().split( "," ) );
                column = referenceColumn = null;

                for ( ColumnEntity subItem : table.getColumns() ) {
                    if ( keyNames.contains( subItem.getJdbcName() ) ) {
                        column = subItem;
                        break;
                    }
                }

                for ( ColumnEntity subItem : tableMapping.get( item.get( "referenced_table_name" ) ).getColumns() ) {
                    if ( subItem.getJdbcName().equalsIgnoreCase( item.get( "referenced_column_name" ) ) ) {
                        referenceColumn = subItem;
                        break;
                    }
                }

                if ( referenceColumn != null ) {
                    table.addReferenceKeys( new ReferenceKeyEntity()
                            .setTable( table )
                            .setColumn( column )
                            .setDatabaseName( "" )
                            .setReferenceColumn( referenceColumn )
                            .setKeyName( item.get( "constraint_name" ) )
                            .setReferenceDatabaseName( item.get( "referenced_table_schema" ) )
                            .setReferenceTable( tableMapping.get( item.get( "referenced_table_name" ) ) ) );
                }
            }
        }
    }

    @Override
    protected void jdbcTypeAliasPatches( Map<String, String> jdbcTypeAliasMappings ) {
        super.jdbcTypeAliasPatches( jdbcTypeAliasMappings );

        jdbcTypeAliasMappings.put( "INT", JdbcType.INTEGER.toString() );
        jdbcTypeAliasMappings.put( "LONGTEXT", JdbcType.CLOB.toString() );
        jdbcTypeAliasMappings.put( "LONGBLOB", JdbcType.BLOB.toString() );
        jdbcTypeAliasMappings.put( "TEXT", JdbcType.LONGVARCHAR.toString() );
        jdbcTypeAliasMappings.put( "DATETIME", JdbcType.TIMESTAMP.toString() );
    }
}
