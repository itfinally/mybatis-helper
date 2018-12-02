package io.github.itfinally.mybatis.paging.interceptor.hook;

import java.util.List;

public interface SqlHook {
  String getPagingSql();

  List<String> getCountingSql();
}
