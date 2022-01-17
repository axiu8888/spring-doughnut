package com.benefitj.system.security;

/**
 * 资源授权处理器
 */
public interface ResourceAuthorizationHandler {

  /**
   * 是否允许访问
   */
  boolean isPermitted(ResourceDescriptor descriptor, String[] types);

}
