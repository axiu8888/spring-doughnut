package com.benefitj.spring.dynamicdatasource.local;

import com.benefitj.spring.dynamicdatasource.DataSourceFactory;
import com.benefitj.spring.dynamicdatasource.DynamicDataSource;
import com.benefitj.spring.dynamicdatasource.LookupKeyContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Collections;

/**
 * 本地缓存的动态数据源配置
 */
@Configuration
public class LocalDynamicDataSourceConfiguration {

  /**
   * 动态数据源
   */
  @Primary
  @ConditionalOnMissingBean(DynamicDataSource.class)
  @Bean
  public DynamicDataSource dynamicDataSource(DataSourceFactory dataSourceFactory,
                                             LookupKeyContext lookupKeyContext) {
    DynamicDataSource dataSource = new DynamicDataSource();
    dataSource.setDataSourceFactory(dataSourceFactory);
    dataSource.setTargetDataSources(Collections.emptyMap());
    dataSource.setLookupKeyContext(lookupKeyContext);
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
   * 数据源工厂
   */
  @ConditionalOnMissingBean
  @Bean
  public LocalDataSourceFactory dataSourceFactory(DataSourceProperties properties) {
    return new LocalDataSourceFactory(properties);
  }

}
