package com.benefitj.spring.aop.log;

import java.util.Map;

public interface HttpLoggingCustomizer {

  /**
   * 是否打印日志，默认为true
   */
  default boolean printable() {
    return true;
  }

  /**
   * 处理请求日志
   *
   * @param handler 处理器
   * @param args    参数
   */
  void customize(HttpLoggingHandler handler, Map<String, Object> args);

  /**
   * 创建日志打印的自定义处理对象
   */
  static HttpLoggingCustomizer newCustomizer() {
    return new SimpleHttpLoggingCustomizer();
  }

}
