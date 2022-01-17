package com.benefitj.system.security;

import java.lang.annotation.*;

/**
 * 资源处理器
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResourceHandler {

  /**
   * 资源名
   */
  String value();

}
