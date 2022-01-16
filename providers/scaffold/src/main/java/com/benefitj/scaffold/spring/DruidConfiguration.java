package com.benefitj.scaffold.spring;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.sql.DataSource;

/**
 * druid数据库配置
 */
//@EnableDynamicDataSourceConfiguration
@EnableTransactionManagement
@Configuration
public class DruidConfiguration {

  /**
   * 事务管理
   */
  @ConditionalOnMissingBean
  @Bean
  public DataSourceTransactionManager transactionManager(DataSource dataSource) {
    DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource);
    manager.setDefaultTimeout(6000);
    manager.setRollbackOnCommitFailure(true);
    return manager;
  }

  @ConditionalOnMissingBean
  @Bean
  public ServletRegistrationBean<Servlet> druidServlet() {
    ServletRegistrationBean<Servlet> bean = new ServletRegistrationBean<>();
    bean.setServlet(new StatViewServlet());
    bean.addUrlMappings("/druid/*");
    // 登录查看信息的账号密码.
    bean.addInitParameter("loginUsername", "admin");
    bean.addInitParameter("loginPassword", "123456");
    return bean;
  }

  @ConditionalOnMissingBean
  @Bean
  public FilterRegistrationBean<Filter> filterRegistrationBean() {
    FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new WebStatFilter());
    bean.addUrlPatterns("/*");
    bean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
    return bean;
  }

}
