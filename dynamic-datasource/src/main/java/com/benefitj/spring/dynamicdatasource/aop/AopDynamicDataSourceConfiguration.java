package com.benefitj.spring.dynamicdatasource.aop;

import com.benefitj.spring.dynamicdatasource.DataSourceFactory;
import com.benefitj.spring.dynamicdatasource.DynamicDataSource;
import com.benefitj.spring.dynamicdatasource.LookupKeyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Collections;

/**
 * Aop实现的动态代理
 */
@Configuration
public class AopDynamicDataSourceConfiguration {

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

  @ConditionalOnMissingBean
  @Bean
  public AopLookupKeyContext lookupKeyContext() {
    return new AopLookupKeyContext();
  }

  /**
   * master数据源
   */
  @ConditionalOnMissingBean
  @Bean
  @ConfigurationProperties("spring.datasource.master")
  public DataSource masterDataSource() {
    return DataSourceBuilder.create().build();
  }

  /**
   * slave数据源
   */
  @ConditionalOnMissingBean
  @Bean
  @ConfigurationProperties("spring.datasource.slave")
  public DataSource slaveDataSource() {
    return DataSourceBuilder.create().build();
  }

  /**
   * 数据源工厂
   */
  @ConditionalOnMissingBean
  @Bean
  public MasterSlaveDataSourceFactory dataSourceFactory(@Qualifier("masterDataSource") DataSource masterDataSource,
                                                        @Qualifier("slaveDataSource") DataSource slaveDataSource,
                                                        @Autowired(required = false) @Qualifier("customDataSource") DataSource customDataSource) {
    return new MasterSlaveDataSourceFactory(masterDataSource, slaveDataSource, customDataSource);
  }

}
