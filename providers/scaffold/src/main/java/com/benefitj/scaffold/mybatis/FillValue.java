package com.benefitj.scaffold.mybatis;

import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface FillValue {

  /**
   * 键
   */
  String key();

  /**
   * 支持的类型
   */
  SqlCommandType[] types();

}
