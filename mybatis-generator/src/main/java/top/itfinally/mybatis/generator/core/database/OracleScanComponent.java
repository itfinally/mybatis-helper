package top.itfinally.mybatis.generator.core.database;

import org.springframework.stereotype.Component;
import top.itfinally.mybatis.generator.core.database.entity.TableEntity;

import java.util.List;

/**
 * <pre>
 * *********************************************
 * All rights reserved.
 * Description: ${类文件描述}
 * *********************************************
 *  Version       Date          Author        Desc ( 一句话描述修改 )
 *  v1.0          2018/7/31       itfinally       首次创建
 * *********************************************
 * </pre>
 */
@Component
public class OracleScanComponent extends DatabaseScanComponent {

    @Override
    public List<TableEntity> getTables() {
        return null;
    }
}
