package com.benefitj.scaffold.mybatis;

import com.benefitj.core.ReflectUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public interface InterceptorHandler {

  /**
   * 拦截
   *
   * @param invocation 调用器
   * @param statement  语句
   * @param type       类型
   * @param parameter  参数
   * @return 返回结果，如果不为空，直接返回此对象，否则继续调用
   * @throws Throwable
   */
  Object intercept(Invocation invocation, MappedStatement statement, SqlCommandType type, Object parameter) throws Throwable;

  /**
   * 获取字段
   *
   * @param type           类型
   * @param annotationType 注解类型
   * @return 返回获取的字段
   */
  default List<Field> getFields(Class<?> type, Class<? extends Annotation> annotationType) {
    return getFields(type, f -> f.isAnnotationPresent(annotationType));
  }

  /**
   * 获取字段
   *
   * @param type   类型
   * @param filter 过滤器
   * @return 返回获取的字段
   */
  default List<Field> getFields(Class<?> type, Predicate<Field> filter) {
    return getFields(type, filter, f -> false);
  }

  /**
   * 获取字段
   *
   * @param type        类型
   * @param filter      过滤器
   * @param interceptor 拦截器
   * @return 返回获取的字段
   */
  default List<Field> getFields(Class<?> type, Predicate<Field> filter, Predicate<Field> interceptor) {
    List<Field> fields = new LinkedList<>();
    ReflectUtils.findFields(type, filter, fields::add, interceptor);
    return fields;
  }

  /**
   * 获取字段的值
   *
   * @param field  字段
   * @param source 对象
   * @return 返回字段的值
   */
  default Object getFieldValue(Field field, Object source) {
    return ReflectUtils.getFieldValue(field, source);
  }

  /**
   * 设置字段的值
   *
   * @param field  字段
   * @param source 对象
   * @param value  字段的值
   */
  default void setFieldValue(Field field, Object source, Object value) {
    ReflectUtils.setFieldValue(field, source, value);
  }

}
