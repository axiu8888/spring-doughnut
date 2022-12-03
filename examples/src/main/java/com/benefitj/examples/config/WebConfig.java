package com.benefitj.examples.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
public class WebConfig {

  /**
   * 跨域
   */
  @ConditionalOnMissingBean
  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    // 允许任何来源
    config.setAllowedOriginPatterns(Collections.singletonList("*"));
    // 允许任何请求头
    config.addAllowedHeader(CorsConfiguration.ALL);
    // 允许任何方法
    config.addAllowedMethod(CorsConfiguration.ALL);
    // 允许凭证
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

}
