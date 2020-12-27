package com.benefitj.spring.security.url;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * 默认忽略的路径
 */
public class DefaultPermittedUrlRegistryConfigurerCustomizer implements UrlRegistryConfigurerCustomizer {

  @Override
  public void customize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
    // 允许对于网站静态资源的无授权访问
    registry.antMatchers(
        "/**/*.html",
        "/**/*.css",
        "/**/*.js",
        "/favicon.ico",
        "/swagger-ui.html",
        "/v2/api-docs",
        "/swagger-resources/**",
        "/actuator/**",
        "/favicon.ico"
    ).permitAll();
  }

}
