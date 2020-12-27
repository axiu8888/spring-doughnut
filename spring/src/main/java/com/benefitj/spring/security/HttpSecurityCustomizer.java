package com.benefitj.spring.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 自定义 HttpSecurity 处理
 */
public interface HttpSecurityCustomizer {

  /**
   * 处理
   *
   * @param http Http Security
   * @throws Exception
   */
  void customize(HttpSecurity http) throws Exception;

}
