package com.benefitj.spring.security.url;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import java.util.stream.Stream;

/**
 * 默认忽略的路径
 */
public class DefaultPermittedUrlRegistryConfigurerCustomizer implements UrlRegistryConfigurerCustomizer {

  /**
   * 忽略的路径
   */
  @Value("#{ @environment['spring.security.url.ignore-path'] ?: '' }")
  private String ignorePath;

  @Override
  public void customize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
    // 允许对于网站静态资源的无授权访问
    String[] ignoreUris = getIgnoreUris();
    registry.antMatchers(ignoreUris).permitAll();
  }

  public String[] getIgnoreUris() {
    String[] split = StringUtils.isNotBlank(ignorePath) ? ignorePath.split(",") : null;
    if (split != null) {
      return Stream.of(split, IGNORE_URIS)
          .filter(StringUtils::isNoneBlank)
          .flatMap(Stream::of)
          .filter(StringUtils::isNotBlank)
          .distinct()
          .toArray(String[]::new);
    }
    return IGNORE_URIS;
  }

  /**
   * 默认忽略的路径
   */
  public static final String[] IGNORE_URIS = new String[]{
      "/**/*.html",
      "/**/*.css",
      "/**/*.js",
      "/**/*.html",
      "/**/swagger-resources/**",
      "/**/api-docs/**",
      "/**/swagger-ui/**",
      "/**/actuator/**",
      "/**/favicon.ico"
  };

}
