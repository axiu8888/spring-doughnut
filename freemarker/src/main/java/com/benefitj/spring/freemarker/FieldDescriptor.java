package com.benefitj.spring.freemarker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * 属性模板
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldDescriptor {

  /**
   * 访问修饰符
   */
  @Builder.Default
  private ModiferType modifier = ModiferType.PRIVATE;
  /**
   * 类型
   */
  private Class<?> type;
  /**
   * 名称
   */
  private String name;
  /**
   * 描述
   */
  private String description;
  /**
   * 注解
   */
  @Builder.Default
  private List<AnnotationDescriptor> annotations = new LinkedList<>();

}
