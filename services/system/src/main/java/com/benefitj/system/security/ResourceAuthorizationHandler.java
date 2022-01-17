package com.benefitj.system.security;

import com.benefitj.spring.mvc.mapping.ApiDescriptor;

/**
 * 资源授权处理器
 */
public interface ResourceAuthorizationHandler {

  /**
   * 是否允许访问
   */
  boolean isPermitted(ApiDescriptor descriptor, String[] types);

}
