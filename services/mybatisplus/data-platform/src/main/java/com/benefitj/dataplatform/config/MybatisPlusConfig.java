package com.benefitj.dataplatform.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.benefitj.spring.datasource.DataSourceRouter;
import com.benefitj.spring.datasource.DynamicRoutingDataSource;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

  @ConfigurationProperties(prefix = "spring.datasource.druid")
  @ConditionalOnMissingBean
  @Bean
  public DruidDataSource druidDataSource() {
    return DruidDataSourceBuilder.create().build();
  }

  /**
   * 多数据源动态切换
   */
  @ConditionalOnMissingBean
  @Bean
  public DataSourceRouter dataSourceRouter(DruidDataSource druidDataSource) {
    return new DataSourceRouter() {
      @Override
      public DataSource route(Map<String, Object> args) {

        return null;
      }
    };
  }

  /**
   * 动态数据源切换
   */
  @ConditionalOnMissingBean
  @Bean
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
