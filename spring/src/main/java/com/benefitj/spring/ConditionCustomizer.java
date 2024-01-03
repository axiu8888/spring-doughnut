package com.benefitj.spring;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

import static org.springframework.util.ClassUtils.forName;

/**
 * 自定义条件判断
 */
public interface ConditionCustomizer extends Condition {

  @Override
  boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);


  /**
   * 类是否出现
   *
   * @param className   类名
   * @param classLoader ClassLoader
   * @return 判断结果
   */
  static boolean isPresent(String className, ClassLoader classLoader) {
    if (classLoader == null) {
      classLoader = ClassUtils.getDefaultClassLoader();
    }
    try {
      forName(className, classLoader);
      return true;
    } catch (Throwable ignored) {
      return false;
    }
  }

}
