package com.benefitj.spring.datasource;

import javax.sql.DataSource;
import java.util.Map;


/**
 * 数据源路由
 */
public interface DataSourceRouter {

  /**
   * 数据源
   *
   * @param args 参数
   * @return 返回数据源
   */
  DataSource route(Map<String, Object> args);

}
