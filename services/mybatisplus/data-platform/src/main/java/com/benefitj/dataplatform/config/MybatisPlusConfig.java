package com.benefitj.dataplatform.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.benefitj.spring.datasource.DataSourceRouter;
import com.benefitj.spring.datasource.DynamicRoutingDataSource;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;


@Slf4j
//@EntityScan("com.benefitj.mybatisplus.entity")
//@MapperScan("com.benefitj.mybatisplus.dao.mapper")
@Configuration
public class MybatisPlusConfig {

  /**
   * 多数据源动态切换
   */
  @ConditionalOnMissingBean
  @Bean
  public DataSourceRouter dataSourceRouter(@Qualifier("druidDataSource") DruidDataSource druidDataSource) {
    return new DataSourceRouter() {
      @Override
      public DataSource route(Map<String, Object> args) {
        // 动态切换数据源
        return druidDataSource;
      }
    };
  }

  /**
   * 动态数据源切换
   */
  @Primary
  //@ConditionalOnMissingBean(name="dataSource")
  @Bean("dataSource")
  public DataSource dynamicDataSource(DataSourceRouter router) {
    return new DynamicRoutingDataSource(router);
  }


  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new DynamicTableNameInnerInterceptor() {{
      setTableNameHandler((sql, tableName) -> {
        // 动态表名
        //log.info("table: {}, sql: {}", tableName, sql);
        return tableName;
      });
    }});
    return interceptor;
  }

  /**
   * pagehelper的分页插件
   */
  @Bean
  public PageInterceptor pageInterceptor() {
    return new PageInterceptor();
  }

}
