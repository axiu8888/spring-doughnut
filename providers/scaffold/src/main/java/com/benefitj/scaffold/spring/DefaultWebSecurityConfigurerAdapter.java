package com.benefitj.scaffold.spring;

import com.benefitj.scaffold.security.*;
import com.benefitj.spring.security.AbstractWebSecurityConfigurerAdapter;
import com.benefitj.spring.security.EnableHttpSecurityCustomizerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@EnableHttpSecurityCustomizerConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class DefaultWebSecurityConfigurerAdapter extends AbstractWebSecurityConfigurerAdapter {

  /**
   * 装载BCrypt密码编码器
   */
  @ConditionalOnMissingBean
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 跨域
   */
  @ConditionalOnMissingBean
  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    //config.addAllowedOrigin("*");
    config.addAllowedOriginPattern("*");
    config.addAllowedHeader("*");
    config.setMaxAge(36000L);
    config.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
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

  /**
   * token认证的过滤器
   */
  @ConditionalOnMissingBean
  @Bean
  public JwtTokenAuthenticationProcessingFilter jwtTokenAuthenticationProcessingFilter() {
    return new JwtTokenAuthenticationProcessingFilter();
  }

  /**
   * token认证失败的处理
   */
  @ConditionalOnMissingBean
  @Bean
  public JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler() {
    return new JwtAuthenticationFailureHandler();
  }

  /**
   * token认证
   */
  @ConditionalOnMissingBean
  @Bean
  public JwtAuthenticationProvider jwtAuthenticationProvider() {
    return new JwtAuthenticationProvider();
  }

  @Lazy
  @Autowired
  public void configureAuthentication(AuthenticationManagerBuilder builder,
                                      JwtUserDetailsService userDetailsService,
                                      PasswordEncoder passwordEncoder) throws Exception {
    builder
        // 设置UserDetailsService
        .userDetailsService(userDetailsService)
        // 使用BCrypt进行密码的hash
        .passwordEncoder(passwordEncoder);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(jwtAuthenticationProvider());
  }

  @Override
  protected void configure0(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        // 由于使用的是JWT，我们这里不需要csrf
        .csrf().disable()
        // 基于token，所以不需要session
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests().anyRequest().authenticated();
    // 配置filter
    loadFilters(httpSecurity);
    // 禁用缓存
    httpSecurity.headers().cacheControl().disable();
  }

  /**
   * 配置filter
   *
   * @param httpSecurity
   */
  public void loadFilters(HttpSecurity httpSecurity) {
    httpSecurity.addFilterBefore(jwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
    httpSecurity.addFilterBefore(corsFilter(), JwtTokenAuthenticationProcessingFilter.class);
  }

}
