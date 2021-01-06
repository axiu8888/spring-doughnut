package com.benefitj.spring.dynamicdatasource;

import javax.sql.DataSource;

/**
 * 数据源工厂
 */
public interface DataSourceFactory {

  /**
   * 创建数据源
   *
   * @param lookupKey KEY
   * @return 返回数据源
   */
  DataSource create(Object lookupKey) throws Exception;

}
