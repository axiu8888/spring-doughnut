package com.benefitj.spring.dynamicdatasource.local;

import com.alibaba.druid.pool.DruidDataSource;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.dynamicdatasource.DataSourceFactory;
import com.benefitj.spring.dynamicdatasource.JdbcUrl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

/**
 * 数据源工厂
 */
public class LocalDataSourceFactory implements DataSourceFactory {

  private DataSourceProperties properties;

  public LocalDataSourceFactory(DataSourceProperties properties) {
    this.setProperties(properties);
  }

  @Override
  public DataSource create(Object lookupKey) throws Exception {
    if (lookupKey instanceof DataSource) {
      return (DataSource) lookupKey;
    }

    // TODO 2021-01-06 查找数据库，如果不存在，直接返回null

    // DruidDataSourceBuilder

    DataSourceProperties props = BeanHelper.copy(this.getProperties());
    JdbcUrl jdbcUrl = parseToUrl(props.getUrl(), (String) lookupKey);

    DruidDataSource dataSource = DataSourceBuilder.create()
        .username(props.getUsername())
        .password(props.getPassword())
        .driverClassName(props.getDriverClassName())
        .url(jdbcUrl.toJdbc())
        .type(DruidDataSource.class)
        .build();

    dataSource.setMaxActive(30);
    dataSource.setInitialSize(10);
    dataSource.setValidationQuery("SELECT 1");
    dataSource.setTestOnBorrow(true);

    return dataSource;
  }


  public DataSourceProperties getProperties() {
    return properties;
  }

  public void setProperties(DataSourceProperties properties) {
    this.properties = properties;
  }

  public JdbcUrl parseToUrl(String url, String db) {
    JdbcUrl jdbcUrl = JdbcUrl.parse(url);
    if (StringUtils.isNotBlank(db)) {
      jdbcUrl.setDatabase(db);
    }
    return jdbcUrl;
  }

}
