package com.benefitj.system.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.benefitj.scaffold.mybatis.InterceptorHandler;
import com.benefitj.scaffold.mybatis.MybatisInterceptor;
import com.benefitj.scaffold.spring.EnableDruidConfuration;
import com.benefitj.scaffold.mybatis.FillValueHandler;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@EntityScan("com.benefitj.system.model")
@MapperScan("com.benefitj.system.mapper")
@EnableDruidConfuration       // druid
@Configuration
public class MybatisPlusConfig {

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

//  /**
//   * pagehelper的分页插件
//   */
//  @Bean
//  public PageInterceptor pageInterceptor() {
//    return new PageInterceptor();
//  }

  @Bean
  public MybatisInterceptor mybatisInterceptor(@Autowired(required = false) List<InterceptorHandler> handlers) {
    return new MybatisInterceptor(handlers);
  }

  @Bean
  public InterceptorHandler defaultValueFill() {
    return new FillValueHandler();
  }

}
