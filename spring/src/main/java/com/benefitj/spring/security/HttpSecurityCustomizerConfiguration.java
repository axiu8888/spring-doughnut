package com.benefitj.spring.security;

import com.benefitj.spring.security.url.AnnotationUrlRegistryConfigurerCustomizer;
import com.benefitj.spring.security.url.DefaultPermittedUrlRegistryConfigurerCustomizer;
import com.benefitj.spring.security.url.UrlRegistryHttpSecurityCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP Security 配置
 */
@Configuration
public class HttpSecurityCustomizerConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public HttpSecurityConfigurerAdapterProcessor httpSecurityConfigurerAdapterProcessor() {
    return new HttpSecurityConfigurerAdapterProcessor();
  }

  /**
   * 根据注解忽略的路径
   */
  @ConditionalOnMissingBean
  @Bean
  public AnnotationUrlRegistryConfigurerCustomizer annotationUrlAuthorizationConfigurerCustomizer() {
    return new AnnotationUrlRegistryConfigurerCustomizer();
  }

  /**
   * 默认忽略的路径
   */
  @ConditionalOnMissingBean
  @Bean
  public DefaultPermittedUrlRegistryConfigurerCustomizer defaultUrlAuthorizationConfigurerCustomizer() {
    return new DefaultPermittedUrlRegistryConfigurerCustomizer();
  }

  /**
   * 处理URL的认证
   */
  @ConditionalOnMissingBean
  @Bean
  public UrlRegistryHttpSecurityCustomizer urlRegistryHttpSecurityCustomizer() {
    return new UrlRegistryHttpSecurityCustomizer();
  }

}
