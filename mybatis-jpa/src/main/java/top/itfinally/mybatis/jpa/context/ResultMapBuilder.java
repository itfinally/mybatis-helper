package top.itfinally.mybatis.jpa.context;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import top.itfinally.mybatis.jpa.entity.AttributeMetadata;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;
import top.itfinally.mybatis.jpa.entity.ForeignAttributeMetadata;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/11/3       itfinally       首次创建
 * *********************************************
 * </pre>
 */
public class ResultMapBuilder {
    private final static String PREFIX = ResultMapFactory.ResultMapToken.PREFIX;

    private ResultMapBuilder() {
    }

    public static String build( ResultMapFactory.ResultMapToken token, EntityMetadata metadata ) {
        Map<Class<?>, ResultMapMark> resultMaps = new HashMap<>();
        List<SelectMark> noResultSelections = new ArrayList<>();
        List<SelectMark> copier;

        ResultMapMark mainResultMap = buildResultMap( PREFIX + token.getHashedCacheKey(), metadata, noResultSelections );
        resultMaps.put( metadata.getEntityClass(), mainResultMap );

        ResultMapMark resultMap;
        List<SelectMark> completedSelections = new ArrayList<>();

        while ( !noResultSelections.isEmpty() ) {
            copier = new ArrayList<>( noResultSelections );
            noResultSelections.clear();

            for ( SelectMark selection : copier ) {
                if ( resultMaps.containsKey( selection.getType() ) ) {
                    selection.setResultId( resultMaps.get( selection.getType() ).getId() );
                    completedSelections.add( selection );
                    continue;
                }

                resultMap = buildResultMap( selection.getResultId(), selection.getMetadata(), noResultSelections );
                resultMaps.put( selection.getMetadata().getEntityClass(), resultMap );
                completedSelections.add( selection );
            }
        }

        return new Mapper( token.getNamespace(), new ArrayList<>( resultMaps.values() ), completedSelections ).toString();
    }

    private static ResultMapMark buildResultMap( String resultMapId, EntityMetadata metadata, List<SelectMark> noResultSelections ) {
        List<? super ResultMark> results = new ArrayList<>();
        for ( AttributeMetadata column : metadata.getColumns() ) {
            results.add( new ResultMark( column ) );
        }

        ResultMark result;

        for ( ForeignAttributeMetadata foreignColumn : metadata.getReferenceColumns() ) {
            if ( foreignColumn.isCollection() ) {
                result = new CollectionMark( foreignColumn );
                noResultSelections.add( ( ( CollectionMark ) result ).getSelect() );

            } else {
                result = new AssociationMark( foreignColumn );
                noResultSelections.add( ( ( AssociationMark ) result ).getSelect() );
            }

            results.add( result );
        }

        return new ResultMapMark( resultMapId, metadata.getEntityClass(), results );
    }
}

interface Priority {
    int getPriority();
}

class Mapper {
    private final List<ResultMapMark> resultMaps;
    private final List<SelectMark> selects;
    private final String namespace;

    public Mapper( String namespace, List<ResultMapMark> resultMaps, List<SelectMark> selects ) {
        this.resultMaps = resultMaps;
        this.selects = selects;
        this.namespace = namespace;
    }

    @Override
    public String toString() {
        List<String> resultMapClauses = new ArrayList<>();
        for ( ResultMapMark resultMap : resultMaps ) {
            resultMapClauses.add( resultMap.toString() );
        }

        List<String> selectionClauses = new ArrayList<>();
        for ( SelectMark selection : selects ) {
            selectionClauses.add( selection.toString() );
        }

        return Joiner.on( "\n" ).join( Lists.newArrayList(

                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">",
                String.format( "<mapper namespace=\"%s\">", namespace ),

                "${resultMaps}",
                "",
                "${selections}",

                "</mapper>"

        ) ).replaceFirst( "\\$\\{resultMaps}", Joiner.on( "\n\n" ).join( resultMapClauses ) )
                .replaceFirst( "\\$\\{selections}", Joiner.on( "\n\n" ).join( selectionClauses ) );
    }
}

class ResultMapMark {
    private final String id;
    private final Class<?> type;
    private final List<? super ResultMark> results;

    public ResultMapMark( String id, Class<?> type, List<? super ResultMark> results ) {
        this.id = id;
        this.type = type;
        this.results = results;
    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public String toString() {
        Collections.sort( ( ( List<Priority> ) results ), new Comparator<Priority>() {
            @Override
            @ParametersAreNonnullByDefault
            public int compare( Priority before, Priority after ) {
                return Integer.compare( before.getPriority(), after.getPriority() );
            }
        } );

        List<String> resultClauses = new ArrayList<>();
        for ( Object result : results ) {
            resultClauses.add( result.toString() );
        }

        return Joiner.on( "\n" ).join( Lists.newArrayList(
                String.format( "  <resultMap id=\"%s\" type=\"%s\">", id, type.getName() ),
                "${results}",
                "  </resultMap>"
        ) ).replaceFirst( "\\$\\{results}", Joiner.on( "\n" ).join( resultClauses ) );
    }
}

class ResultMark implements Priority {
    private final boolean id;
    private final String javaName;
    private final String jdbcName;

    ResultMark( AttributeMetadata metadata ) {
        this.id = metadata.isPrimary();
        this.javaName = metadata.getJavaName();
        this.jdbcName = metadata.getJdbcName();
    }

    public boolean isId() {
        return id;
    }

    public String getJavaName() {
        return javaName;
    }

    String getJdbcName() {
        return jdbcName;
    }

    @Override
    public String toString() {
        return String.format( "    <%s property=\"%s\" column=\"%s\"/>", id ? "id" : "result", javaName, jdbcName );
    }

    @Override
    public int getPriority() {
        return id ? 0 : 1;
    }
}

class AssociationMark extends ResultMark implements Priority {
    private String selectId = String.format( "selectNo_%s", UUID.randomUUID().toString().replaceAll( "-", "" ) );
    private ForeignAttributeMetadata metadata;

    AssociationMark( ForeignAttributeMetadata metadata ) {
        super( metadata );

        this.metadata = metadata;
    }

    public SelectMark getSelect() {
        return new SelectMark( selectId, metadata );
    }

    @Override
    public String toString() {
        return String.format( "    <association property=\"%s\" column=\"%s\" select=\"%s\" fetchType=\"%s\"/>",
                getJavaName(), getJdbcName(), selectId, metadata.isLazy() ? "lazy" : "eager" );
    }

    @Override
    public int getPriority() {
        return 2;
    }
}

class CollectionMark extends ResultMark implements Priority {
    private String selectId = String.format( "selectNo_%s", UUID.randomUUID().toString().replaceAll( "-", "" ) );
    private ForeignAttributeMetadata metadata;

    public CollectionMark( ForeignAttributeMetadata metadata ) {
        super( metadata );

        this.metadata = metadata;
    }

    public SelectMark getSelect() {
        return new SelectMark( selectId, metadata );
    }

    @Override
    public String toString() {
        return String.format( "    <collection property=\"%s\" column=\"%s\" ofType=\"%s\" select=\"%s\" fetchType=\"%s\"/>",
                getJavaName(), getJdbcName(), metadata.getEntityMetadata().getEntityClass().getName(),
                selectId, metadata.isLazy() ? "lazy" : "eager" );
    }

    @Override
    public int getPriority() {
        return 3;
    }
}

class SelectMark {
    private final String id;
    private final Class<?> type;
    private final String targetTable;
    private final String targetField;
    private final String targetAttribute;

    private final EntityMetadata metadata;

    private String resultId = String.format( "nestResultMap_%s", UUID.randomUUID().toString().replaceAll( "-", "" ) );

    SelectMark( String id, ForeignAttributeMetadata metadata ) {
        this.id = id;
        this.type = metadata.isMap() ? Map.class : metadata.getEntityMetadata().getEntityClass();
        this.targetTable = metadata.getEntityMetadata().getTableName();
        this.targetField = metadata.getReferenceAttributeMetadata().getJdbcName();
        this.targetAttribute = metadata.getReferenceAttributeMetadata().getJavaName();
        this.metadata = metadata.getEntityMetadata();
    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    public String getResultId() {
        return resultId;
    }

    public EntityMetadata getMetadata() {
        return metadata;
    }

    public SelectMark setResultId( String resultId ) {
        this.resultId = resultId;
        return this;
    }

    @Override
    public String toString() {
        return Joiner.on( "\n" ).join( Lists.newArrayList(
                String.format( "  <select id=\"%s\" resultMap=\"%s\">", id, resultId ),
                String.format( "    select * from %s where %s = #{%s}", targetTable, targetField, targetAttribute ),
                "  </select>"
        ) );
    }
}