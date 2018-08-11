package top.itfinally.mybatis.generator.core.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.itfinally.mybatis.generator.core.database.entity.ColumnEntity;
import top.itfinally.mybatis.generator.core.database.entity.ReferenceKeyEntity;
import top.itfinally.mybatis.generator.core.database.entity.TableEntity;
import top.itfinally.mybatis.generator.core.database.entity.UniqueKeyEntity;
import top.itfinally.mybatis.generator.exception.UnknownNameMappingException;

import java.util.*;

/**
 * <pre>
 * *********************************************
 * Copyright BAIBU.
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
    private Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    public List<TableEntity> getTables() {
        List<Map<String, String>> tables = Builder.informationMapper.getTables();
        Map<String, TableEntity> tableMapping = new HashMap<>( tables.size() );
        List<TableEntity> tableInfoList = new ArrayList<>( tables.size() );

        TableEntity table;
        for ( Map<String, String> item : tables ) {
            table = extractColumnsInfo( extractTableInfo( item ) );
            tableMapping.put( table.getJdbcName(), table );
            tableInfoList.add( table );
        }

        for ( TableEntity item : tableInfoList ) {
            relationshipAnalyzing( item, tableMapping );
        }

        return tableInfoList;
    }

    private TableEntity extractTableInfo( Map<String, String> tableMetadata ) {
        TableEntity table = new TableEntity()
                .setJdbcName( ( tableMetadata.get( "TABLE_NAME" ) ).toLowerCase() )
                .setComment( StringUtils.isEmpty( tableMetadata.get( "TABLE_COMMENT" ) ) ? "" : ( tableMetadata.get( "TABLE_COMMENT" ) ).toLowerCase() );

        if ( null == namingMapping ) {
            logger.warn( "There are no naming mapping to offer, convert underline-case to camel-case by default." );

            table.setJavaName( namingConverter.convert( table.getJdbcName() ).replaceAll( "^\\w",
                    Character.toString( table.getJdbcName().charAt( 0 ) ).toUpperCase() ) );

        } else {
            String javaName = namingMapping.getMapping( table.getJdbcName() );
            if ( StringUtils.isEmpty( javaName ) ) {
                throw new UnknownNameMappingException( String.format( "No mapping found for table '%s'", table.getJdbcName() ) );
            }

            table.setJavaName( javaName );
        }

        return table;
    }

    private TableEntity extractColumnsInfo( TableEntity table ) {
        List<Map<String, String>> columns = Builder.informationMapper.getColumns( table.getJdbcName() );
        Set<ColumnEntity> columnSet = new HashSet<>( columns.size() );
        ColumnEntity column;

        TypeMapping typeMapping;
        for ( Map<String, String> item : columns ) {
            column = new ColumnEntity()
                    .setComment( item.get( "COLUMN_COMMENT" ) )
                    .setJdbcType( item.get( "DATA_TYPE" ).toLowerCase() )
                    .setJdbcName( item.get( "COLUMN_NAME" ).toLowerCase() )
                    .setNotNull( "no".equalsIgnoreCase( item.get( "IS_NULLABLE" ) ) )
                    .setPrimaryKey( "pri".equalsIgnoreCase( item.get( "COLUMN_KEY" ) ) )
                    .setJavaName( namingConverter.convert( ( item.get( "COLUMN_NAME" ) ).toLowerCase() ) );

            typeMapping = Builder.initTypeMapping( column );
            buildGS( column.setJavaTypeClass( typeMapping.getJavaType() )
                    .setJavaType( typeMapping.getJavaType().getSimpleName() ) );

            table.setPrimaryKey( column );
            columnSet.add( column );
        }

        return table.setColumns( columnSet );
    }

    private void relationshipAnalyzing( TableEntity table, Map<String, TableEntity> tableMapping ) {
        List<Map<String, String>> tableKeys = Builder.informationMapper.getTableKeys( table.getJdbcName() );

        ColumnEntity column;
        Set<String> keyNames;
        ColumnEntity[] columns;
        List<String> indexTable;
        ColumnEntity referenceColumn;

        for ( Map<String, String> item : tableKeys ) {
            if ( "unique".equalsIgnoreCase( item.get( "constraint_type" ) ) ) {
                indexTable = Lists.newArrayList( item.get( "columns" ).toLowerCase().split( "," ) );
                columns = new ColumnEntity[ indexTable.size() ];
                keyNames = Sets.newHashSet( indexTable );

                for ( ColumnEntity subItem : table.getColumns() ) {
                    if ( keyNames.contains( subItem.getJdbcName() ) ) {
                        columns[ indexTable.indexOf( subItem.getJdbcName() ) ] = subItem;
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
    protected void typePatch( Map<String, String> typePatches ) {
        super.typePatch( typePatches );

        typePatches.put( "INT", JdbcType.INTEGER.toString() );
        typePatches.put( "LONGTEXT", JdbcType.CLOB.toString() );
        typePatches.put( "LONGBLOB", JdbcType.BLOB.toString() );
        typePatches.put( "TEXT", JdbcType.LONGVARCHAR.toString() );
        typePatches.put( "DATETIME", JdbcType.TIMESTAMP.toString() );
    }
}
