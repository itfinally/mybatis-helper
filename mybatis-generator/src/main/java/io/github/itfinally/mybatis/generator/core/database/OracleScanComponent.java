package io.github.itfinally.mybatis.generator.core.database;

import org.springframework.stereotype.Component;
import io.github.itfinally.mybatis.generator.core.database.entity.TableEntity;

import java.util.List;

@Component
public class OracleScanComponent extends DatabaseScanComponent {

  @Override
  public List<TableEntity> getTables() {
    return null;
  }
}
