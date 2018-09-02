package top.itfinally.mybatis.jpa.sql;

import com.google.common.base.Function;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.springframework.stereotype.Component;
import top.itfinally.mybatis.jpa.entity.EntityMetadata;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

import static top.itfinally.mybatis.jpa.mapper.BasicCrudMapper.*;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/8/28       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Component
public class MysqlCrudSqlCreator extends BasicCrudSqlCreator {

    protected MysqlCrudSqlCreator( Configuration configuration ) {
        super( configuration );
    }

    @Override
    public BoundSql queryByIdIs( final EntityMetadata metadata, final Object unknownArgs ) {
        return buildBoundSql( unknownArgs, new Function<XMLLanguageDriver, SqlSource>() {
            @Override
            @ParametersAreNonnullByDefault
            public SqlSource apply( XMLLanguageDriver xmlLanguageDriver ) {
                return xmlLanguageDriver.createSqlSource( configuration, String.format( "select * from %s where %s = #{%s}",
                        metadata.getTableName(), metadata.getId().getJdbcName(), ID ), getSpecifyParameterType( ID, unknownArgs ) );
            }
        } );
    }

    @Override
    public BoundSql queryByIdIn( final EntityMetadata metadata, final Object unknownArgs ) {
        assertInExpressionNotEmpty( IDS, "queryByIdIn", unknownArgs );

        return buildBoundSql( unknownArgs, new Function<XMLLanguageDriver, SqlSource>() {
            @Override
            @ParametersAreNonnullByDefault
            public SqlSource apply( XMLLanguageDriver xmlLanguageDriver ) {
                return xmlLanguageDriver.createSqlSource( configuration, "<script> " +
                        String.format( " select * from %s where %s in ", metadata.getTableName(), metadata.getId().getJdbcName() ) +
                        String.format( " <foreach collection=\"%s\" item=\"id\" open=\"(\" separator=\",\" close=\")\"> ", IDS ) +
                        " #{id} " +
                        " </foreach> " +
                        " </script> ", getSpecifyParameterType( IDS, unknownArgs ) );
            }
        } );
    }

    @Override
    public BoundSql queryAll( final EntityMetadata metadata ) {
        return buildBoundSql( null, new Function<XMLLanguageDriver, SqlSource>() {
            @Override
            @ParametersAreNonnullByDefault
            public SqlSource apply( XMLLanguageDriver xmlLanguageDriver ) {
                return xmlLanguageDriver.createSqlSource( configuration, String.format(
                        "select * from %s", metadata.getTableName() ), Object.class );
            }
        } );
    }

    @Override
    public BoundSql existByIdIs( final EntityMetadata metadata, final Object unknownArgs ) {
        return buildBoundSql( null, new Function<XMLLanguageDriver, SqlSource>() {
            @Override
            @ParametersAreNonnullByDefault
            public SqlSource apply( XMLLanguageDriver xmlLanguageDriver ) {
                return xmlLanguageDriver.createSqlSource( configuration, String.format( "select 1 from %s where %s = #{%s}",
                        metadata.getTableName(), metadata.getId().getJdbcName(), ID ), getSpecifyParameterType( ID, unknownArgs ) );
            }
        } );
    }

    @Override
    public BoundSql save( EntityMetadata metadata, Object unknownArgs ) {
        return actualSave( metadata, unknownArgs, false );
    }

    @Override
    public BoundSql saveWithNonnull( EntityMetadata metadata, Object unknownArgs ) {
        return actualSave( metadata, unknownArgs, true );
    }

    private BoundSql actualSave( final EntityMetadata metadata, final Object unknownArgs, boolean isNonnull ) {
        final Object applier = Objects.requireNonNull( getSpecifyParameter( ENTITY, unknownArgs ), "BasicCrudMapper.save expect an entity but got null." );
        final InsertPair insertPair = createInsertFieldAndValues( metadata, applier, isNonnull );

        return buildBoundSql( unknownArgs, new Function<XMLLanguageDriver, SqlSource>() {
            @Override
            @ParametersAreNonnullByDefault
            public SqlSource apply( XMLLanguageDriver xmlLanguageDriver ) {
                return xmlLanguageDriver.createSqlSource( configuration, String.format( "insert into %s( %s ) values( %s )",
                        metadata.getTableName(), insertPair.getFields(), insertPair.getValues() ), getSpecifyParameterType( ENTITY, unknownArgs ) );
            }
        } );
    }

    @Override
    public BoundSql saveAll( final EntityMetadata metadata, final Object unknownArgs ) {
        final InsertPair insertPair = createInsertFieldAndValues( metadata, null, false );

        return buildBoundSql( unknownArgs, new Function<XMLLanguageDriver, SqlSource>() {
            @Override
            @ParametersAreNonnullByDefault
            public SqlSource apply( XMLLanguageDriver xmlLanguageDriver ) {
                return xmlLanguageDriver.createSqlSource( configuration, "<script>" +
                        String.format( " insert into %s( %s ) values ", metadata.getTableName(), insertPair.getFields() ) +
                        String.format( " <foreach collection=\"%s\" item=\"%s\" open=\"(\" separator=\"), (\" close=\")\"> ", ENTITIES, ENTITY ) +
                        insertPair.getValues() +
                        " </foreach> " +
                        " </script> ", getSpecifyParameterType( ENTITY, unknownArgs ) );
            }
        } );
    }

    @Override
    public BoundSql updateByIdIs( EntityMetadata metadata, Object unknownArgs ) {
        return actualUpdate( metadata, unknownArgs, false );
    }

    @Override
    public BoundSql updateWithNonnullByIdIs( EntityMetadata metadata, Object unknownArgs ) {
        return actualUpdate( metadata, unknownArgs, true );
    }

    private BoundSql actualUpdate( final EntityMetadata metadata, final Object unknownArgs, boolean isNonnull ) {
        final Object applier = Objects.requireNonNull( getSpecifyParameter( ENTITY, unknownArgs ), "BasicCrudMapper.save expect an entity but got null." );
        final String updateFieldAndValues = createUpdateFieldAndValues( metadata, applier, isNonnull );

        return buildBoundSql( unknownArgs, new Function<XMLLanguageDriver, SqlSource>() {
            @Override
            @ParametersAreNonnullByDefault
            public SqlSource apply( XMLLanguageDriver xmlLanguageDriver ) {
                return xmlLanguageDriver.createSqlSource( configuration, String.format( "update %s set %s where %s = #{%s.%s}",
                        metadata.getTableName(), updateFieldAndValues, metadata.getId().getJdbcName(),
                        ENTITY, metadata.getId().getJavaName() ), getSpecifyParameterType( ENTITY, unknownArgs ) );
            }
        } );
    }

    @Override
    public BoundSql deleteByIdIs( final EntityMetadata metadata, final Object unknownArgs ) {
        return buildBoundSql( unknownArgs, new Function<XMLLanguageDriver, SqlSource>() {
            @Override
            @ParametersAreNonnullByDefault
            public SqlSource apply( XMLLanguageDriver xmlLanguageDriver ) {
                return xmlLanguageDriver.createSqlSource( configuration, String.format( "delete from %s where %s = #{%s}",
                        metadata.getTableName(), metadata.getId().getJdbcName(), ID ), getSpecifyParameterType( ID, unknownArgs ) );
            }
        } );
    }

    @Override
    public BoundSql deleteAllByIdIn( final EntityMetadata metadata, final Object unknownArgs ) {
        assertInExpressionNotEmpty( IDS, "deleteAllByIdIn", unknownArgs );

        return buildBoundSql( unknownArgs, new Function<XMLLanguageDriver, SqlSource>() {
            @Override
            @ParametersAreNonnullByDefault
            public SqlSource apply( XMLLanguageDriver xmlLanguageDriver ) {
                return xmlLanguageDriver.createSqlSource( configuration, "<script> " +
                        String.format( " delete from %s where %s in ", metadata.getTableName(), metadata.getId().getJdbcName() ) +
                        String.format( " <foreach collection=\"%s\" item=\"id\" open=\"(\" separator=\",\" close=\")\"> ", IDS ) +
                        " #{id} " +
                        " </foreach> " +
                        " </script> ", getSpecifyParameterType( IDS, unknownArgs ) );
            }
        } );
    }
}
