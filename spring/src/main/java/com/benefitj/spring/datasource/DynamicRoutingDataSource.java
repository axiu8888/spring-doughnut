package com.benefitj.spring.datasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源
 */
public class DynamicRoutingDataSource extends AbstractDataSource {

  private static final Map<String, Object> EMPTY = Collections.unmodifiableMap(new HashMap<>());

  private DataSourceRouter router;

  public DynamicRoutingDataSource() {
  }

  public DynamicRoutingDataSource(DataSourceRouter router) {
    this.setRouter(router);
  }

  public DataSourceRouter getRouter() {
    return router;
  }

  public void setRouter(DataSourceRouter router) {
    this.router = router;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return getConnection(null, null);
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    Map<String, Object> args = StringUtils.isAllBlank(username, password) ? EMPTY : null;
    DataSource dataSource = getRouter().route(args);
    if (StringUtils.isAllBlank(username, password))
      return dataSource.getConnection();
    else
      return dataSource.getConnection(username, password);
  }

}
