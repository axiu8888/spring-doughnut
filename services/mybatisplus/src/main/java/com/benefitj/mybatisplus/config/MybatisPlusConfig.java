package com.benefitj.mybatisplus.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.benefitj.mybatisplus.dao.mybatis.FieldValueCreator;
import com.benefitj.mybatisplus.dao.mybatis.FieldValueFiellInterceptor;
import com.benefitj.mybatisplus.dao.mybatis.FieldValueFiller;
import com.benefitj.mybatisplus.dao.mybatis.SimpleFieldValueFiller;
import com.benefitj.spring.security.jwt.token.JwtToken;
import com.benefitj.spring.security.jwt.token.JwtTokenManager;
import com.github.pagehelper.PageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.SqlCommandType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@EntityScan("com.benefitj.mybatisplus.entity")
@MapperScan("com.benefitj.mybatisplus.dao.mapper")
@Configuration
public class MybatisPlusConfig {

  @Bean
  public FieldValueFiller insertFiller() {
    Map<String, FieldValueCreator> map = new ConcurrentHashMap<>(10);
    map.putIfAbsent("createTime", target -> new Date());
    map.putIfAbsent("createBy", target -> {
      JwtToken token = JwtTokenManager.currentToken(true);
      return token != null ? token.getUserId() : null;
    });
    map.putIfAbsent("active", target -> true);
    return new SimpleFieldValueFiller(map, Collections.singletonList(SqlCommandType.INSERT));
  }

  @Bean
  public FieldValueFiller updateFiller() {
    Map<String, FieldValueCreator> map = new ConcurrentHashMap<>(10);
    map.putIfAbsent("updateTime", target -> new Date());
    map.putIfAbsent("updateBy", target -> {
      JwtToken token = JwtTokenManager.currentToken(true);
      return token != null ? token.getUserId() : null;
    });
    return new SimpleFieldValueFiller(map, Collections.singletonList(SqlCommandType.UPDATE));
  }

  @Bean
  public FieldValueFiellInterceptor fieldValueFiellInterceptor(List<FieldValueFiller> fillers) {
    return new FieldValueFiellInterceptor(fillers);
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
