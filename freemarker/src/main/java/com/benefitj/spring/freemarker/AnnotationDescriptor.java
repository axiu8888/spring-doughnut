package com.benefitj.spring.freemarker;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


/**
 * 注解
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnnotationDescriptor {

  /**
   * 类型
   */
  private Class<?> type;
  /**
   * 注解中的值：@注解(值)
   *
   * 值 =>: method=params
   *
   * 如：@Target(value = ElementType.FIELD)
   */
  private String value;
  /**
   * 引入的类
   */
  private List<Class<?>> imports;

}
