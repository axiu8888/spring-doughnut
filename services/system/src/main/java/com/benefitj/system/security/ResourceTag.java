package com.benefitj.system.security;


import java.lang.annotation.*;

/**
 * 资源标识
 *
 * @author dingxiaun
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ResourceTag {

  /**
   * 资源类型: 菜单、文件、资源
   */
  String value();

  /**
   * 组件名
   */
  String component() default "";

}
