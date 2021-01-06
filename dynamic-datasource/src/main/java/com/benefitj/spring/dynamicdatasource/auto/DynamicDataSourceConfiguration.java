package com.benefitj.spring.dynamicdatasource.auto;

import com.alibaba.druid.pool.DruidDataSource;
import com.benefitj.spring.dynamicdatasource.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Collections;

/**
 * 动态数据源配置
 */
@Configuration
public class DynamicDataSourceConfiguration {

  /**
   * 数据源工厂对象
   */
  @ConditionalOnMissingBean
  @Bean
  public DataSourceFactory dataSourceFactory(DataSourceProperties properties) {
    return new DefaultDataSourceFactory(properties);
  }

  /**
   * 默认数据源
   */
  @ConditionalOnMissingBean
  @Bean
  public DataSource defaultDataSource(DataSourceProperties properties) {
    DruidDataSource dataSource = DataSourceBuilder.create()
        .username(properties.getUsername())
        .password(properties.getPassword())
        .driverClassName(properties.getDriverClassName())
        .url(properties.getUrl())
        .type(DruidDataSource.class)
        .build();
    dataSource.setMaxActive(30);
    dataSource.setInitialSize(10);
    dataSource.setValidationQuery("SELECT 1");
    dataSource.setTestOnBorrow(true);
    return dataSource;
  }

  /**
   * key的上下文
   */
  @ConditionalOnMissingBean
  @Bean
  public LookupKeyContext lookupKeyContext() {
    return ThreadLocalLookupKeyContext.getInstance();
  }

  /**
   * 动态数据源
   */
  @Primary
  @ConditionalOnMissingBean(DynamicDataSource.class)
  @Bean
  public DynamicDataSource dynamicDataSource(DataSourceFactory dataSourceFactory,
                                             DataSource defaultDataSource,
                                             LookupKeyContext lookupKeyContext) {
    DynamicDataSource dataSource = new DynamicDataSource();
    dataSource.setDataSourceFactory(dataSourceFactory);
    dataSource.setTargetDataSources(Collections.emptyMap());
    dataSource.setLookupKeyContext(lookupKeyContext);
    // 默认数据源
    dataSource.setDefaultTargetDataSource(defaultDataSource);
    return dataSource;
  }

}