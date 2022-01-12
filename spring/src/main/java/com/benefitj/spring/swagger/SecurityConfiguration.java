package com.benefitj.spring.swagger;

import com.benefitj.spring.security.url.UrlRegistryConfigurerCustomizer;
import com.benefitj.spring.security.url.UrlRegistryHttpSecurityCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnBean(UrlRegistryHttpSecurityCustomizer.class)
@Configuration
public class SecurityConfiguration {

  /**
   * 忽略 swagger 路径
   */
  @ConditionalOnMissingBean(name = "swaggerIgnorePaths")
  @Bean("swaggerIgnorePaths")
  public UrlRegistryConfigurerCustomizer swaggerIgnorePaths() {
    return registry -> {
      String[] ignorePaths = new String[]{
          "/**/*.html",
          "/**/*.css",
          "/**/*.js",
          "/**/swagger-ui.html",
          "/**/v2/api-docs",
          "/**/v3/api-docs",
          "/**/swagger-resources/**",
          "/**/actuator/**",
          "/**/favicon.ico"
      };
      registry.antMatchers(ignorePaths).permitAll();
    };
  }

}
