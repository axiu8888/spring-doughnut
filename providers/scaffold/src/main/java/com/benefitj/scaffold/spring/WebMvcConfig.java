package com.benefitj.scaffold.spring;

import com.benefitj.scaffold.security.token.JwtProperty;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.mvc.EnableCustomArgumentResolverWebMvcConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableCustomArgumentResolverWebMvcConfigurer
@Configuration
public class WebMvcConfig {

  /**
   * 配置
   */
  @ConditionalOnMissingBean
  @Bean
  public JwtProperty jwtProperty() {
    return new JwtProperty();
  }

  /**
   * JWT token管理
   */
  @ConditionalOnMissingBean
  @Bean
  public JwtTokenManager jwtTokenManager(JwtProperty jwtProperty) {
    return new JwtTokenManager(jwtProperty);
  }

  /**
   * 跨域请求
   */
  @ConditionalOnMissingBean
  @Bean
  public WebMvcConfigurer corsWebMvcConfigurer() {
    return new WebMvcConfigurer(){
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowCredentials(false)
            .allowedOriginPatterns("*")
            .exposedHeaders("*")
            .allowedHeaders("*")
            .maxAge(36000L)
            .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedOrigins("*");
      }
    };
  }

}
