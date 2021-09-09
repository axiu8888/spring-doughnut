package com.benefitj.spring;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.Nullable;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean 工具类
 */
public class BeanHelper {
  /**
   * 创建对象
   *
   * @param klass 类
   * @param <T>   返回的对象类型
   * @return 返回创建的对象
   */
  public static <T> T newInstance(Class<T> klass) {
    try {
      Constructor<?>[] constructors = klass.getConstructors();
      for (Constructor<?> c : constructors) {
        if (c.getParameterCount() == 0) {
          c.setAccessible(true);
          return (T) c.newInstance();
        }
      }
      return (T) klass.newInstance();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   */
  public static <R> R copy(R r) {
    return copy(r, (Class<R>) r.getClass());
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   */
  public static <R, U> U copy(R r, Class<U> targetClass) {
    return copy(r, targetClass, false);
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   *
   * @param r               源对象
   * @param targetClass     目标对象
   * @param ignoreNullValue 是否忽略属性
   * @param <R>             源对象类型
   * @param <U>             目标对象类型
   * @return 返回目标对象
   */
  public static <R, U> U copy(R r, Class<U> targetClass, boolean ignoreNullValue) {
    return copy(r, (U) newInstance(targetClass), ignoreNullValue);
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   *
   * @param r   源对象
   * @param u   目标对象
   * @param <R> 源对象类型
   * @param <U> 目标对象类型
   * @return 返回目标对象
   */
  public static <R, U> U copy(R r, U u) {
    return copy(r, u, (Class<?>) null);
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   *
   * @param r        源对象
   * @param u        目标对象
   * @param editable 可编辑的类
   * @param <R>      源对象类型
   * @param <U>      目标对象类型
   * @return 返回目标对象
   */
  public static <R, U> U copy(R r, U u, @Nullable Class<?> editable) {
    BeanUtils.copyProperties(r, u, editable);
    return u;
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   *
   * @param r                源对象
   * @param u                目标对象
   * @param ignoreProperties 忽略的属性
   * @param <R>              源对象类型
   * @param <U>              目标对象类型
   * @return 返回目标对象
   */
  public static <R, U> U copy(R r, U u, @Nullable String... ignoreProperties) {
    BeanUtils.copyProperties(r, u, ignoreProperties);
    return u;
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   *
   * @param r               源对象
   * @param u               目标对象
   * @param ignoreNullValue 忽略NUll值的属性
   * @param <R>             源对象类型
   * @param <U>             目标对象类型
   * @return 返回目标对象
   */
  public static <R, U> U copy(R r, U u, boolean ignoreNullValue) {
    String[] ignoreProperties = ignoreNullValue ? getNullValuePropertyNames(r).toArray(new String[0]) : null;
    return copy(r, u, ignoreProperties);
  }


  /**
   * 获取值为null的属性
   *
   * @param source 对象
   * @return 返回为NUll的属性
   */
  public static List<PropertyDescriptor> getNullValuePropertyDescriptors(Object source) {
    final BeanWrapper bean = new BeanWrapperImpl(source);
    PropertyDescriptor[] pds = bean.getPropertyDescriptors();
    List<PropertyDescriptor> nullValueList = new ArrayList<>(pds.length / 2);
    for (PropertyDescriptor pd : pds) {
      Object value = bean.getPropertyValue(pd.getName());
      if (value == null) {
        nullValueList.add(pd);
      }
    }
    return nullValueList;
  }

  /**
   * 获取值为null的属性名
   *
   * @param source 对象
   * @return 返回为NUll的属性
   */
  public static List<String> getNullValuePropertyNames(Object source) {
    return getNullValuePropertyDescriptors(source)
        .stream()
        .map(FeatureDescriptor::getName)
        .collect(Collectors.toList());
  }

}