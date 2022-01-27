package com.benefitj.system.security;

import com.benefitj.spring.mvc.mapping.ApiDescriptor;

/**
 * 资源授权处理器
 */
public interface ResourceAuthorizationHandler {

  /**
   * 是否允许访问
   *
   * @param descriptor API描述请求
   * @param args       请求参数
   * @return 返回判断结果
   */
  boolean isPermitted(ApiDescriptor descriptor, Object[] args);

}
