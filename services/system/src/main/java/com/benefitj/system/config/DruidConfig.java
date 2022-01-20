package com.benefitj.system.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.benefitj.spring.security.url.UrlRegistryConfigurerCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(DruidDataSourceAutoConfigure.class)
public class DruidConfig {

  @Bean
  public UrlRegistryConfigurerCustomizer druidUrlIgnores() {
    return registry -> registry.antMatchers("/druid/**").permitAll();
  }

//  /**
//   * 去除Druid监控页面的广告
//   */
//  @ConditionalOnProperty(name = "spring.datasource.druid.stat-view-servlet.enabled", havingValue = "true")
//  @ConditionalOnWebApplication
//  @Bean
//  public FilterRegistrationBean<Filter> removeDruidAdFilter(DruidStatProperties properties) throws IOException {
//    // 获取web监控页面的参数
//    DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
//    // 提取common.js的配置路径
//    String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
//    String commonJsPattern = pattern.replace("\\*", "js/common.js");
//    // 获取common.js, 带有广告的common.js全路径，druid-1.1.14
//    String text = Utils.readFromResource("support/http/resources/js/common.js");
//    // 屏蔽 this.buildFooter(); 不构建广告
//    final String newJs = text.replace(
//        "this.buildFooter();" // 原始脚本，触发构建广告的语句
//        , "//this.buildFooter();" // 替换后的脚本
//    );
//    FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
//    registration.setFilter(removeAdFilter(newJs));
//    registration.addUrlPatterns(commonJsPattern);
//    return registration;
//  }
//
//  /**
//   * 删除druid的广告过滤器
//   *
//   * @author BBF
//   */
//  public static Filter removeAdFilter(String newJs) {
//    return (request, resp, chain) -> {
//      chain.doFilter(request, resp);
//      // 重置缓冲区，响应头不会被重置
//      try {
//        resp.resetBuffer();
//      } catch (Exception ignore) {}
//      resp.getWriter().write(newJs);
//      resp.flushBuffer();
//    };
//  }

}
