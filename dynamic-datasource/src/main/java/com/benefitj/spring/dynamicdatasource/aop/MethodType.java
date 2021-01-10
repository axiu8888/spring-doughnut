package com.benefitj.spring.dynamicdatasource.aop;

/**
 * 方法对应数据原的类型
 */
public enum MethodType {
  /**
   * 写入
   */
  MASTER,
  /**
   * 读取
   */
  SLAVE,
  /**
   * 自定义
   */
  CUSTOM;
}
