package com.benefitj.spring.security.jwt;

import com.benefitj.spring.mvc.EnableCustomArgumentResolverWebMvcConfigurer;
import com.benefitj.spring.security.jwt.token.JwtOptions;
import com.benefitj.spring.security.jwt.token.JwtTokenManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.Map;

@ConditionalOnWebApplication
@EnableCustomArgumentResolverWebMvcConfigurer
@Configuration
public class JwtWebMvcConfig {

  /**
   * 配置
   */
  @ConditionalOnMissingBean
  @Bean
  public JwtOptions jwtProperty() {
    return new JwtOptions();
  }

  /**
   * JWT token管理
   */
  @ConditionalOnMissingBean
  @Bean
  public JwtTokenManager jwtTokenManager(JwtOptions options) {
    JwtTokenManager manager = new JwtTokenManager();
    manager.setJwtOptions(options);
    return manager;
  }

  /**
   * 跨域请求
   */
  @ConditionalOnMissingBean
  @Bean
  public WebMvcConfigurer corsWebMvcConfigurer() {
    return new WebMvcConfigurer() {
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

  /**
   * 装载BCrypt密码编码器
   */
  @ConditionalOnMissingBean
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @ConditionalOnMissingBean
  @Bean
  public PathRequestMatcher pathRequestMatcher() {
    // 跳过的路径
    Map<String, HttpMethod> skipPaths = Collections.singletonMap("/auth/**", null);
    // 处理的路径
    Map<String, HttpMethod> processingPaths = Collections.singletonMap("/**", null);
    return new PathRequestMatcher(skipPaths, processingPaths);
  }

}
