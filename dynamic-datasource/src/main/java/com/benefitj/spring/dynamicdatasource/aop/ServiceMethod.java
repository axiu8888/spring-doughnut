package com.benefitj.spring.dynamicdatasource.aop;

import java.lang.reflect.Method;

public class ServiceMethod {

  /**
   * 方法
   */
  private final Method method;
  /**
   * 是否支持
   */
  private boolean support = false;
  /**
   * 前缀类型
   */
  private MethodType type;

  public ServiceMethod(Method method) {
    this.method = method;
  }

  public Method getMethod() {
    return method;
  }

  public boolean isSupport() {
    return support;
  }

  public void setSupport(boolean support) {
    this.support = support;
  }

  public MethodType getType() {
    return type;
  }

  public void setType(MethodType type) {
    this.type = type;
  }

  public String getMethodName() {
    return getMethod().getName();
  }

}
