package com.benefitj.spring.influxdb;

import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 反射工具
 */
public class ReflectUtils {

  /**
   * 获取父类的泛型参数类型
   */
  public static Class getGenericSuperclassBounds(Class clazz) {
    Type type = clazz.getGenericSuperclass();
    while (!(type instanceof Class)) {
      if (type instanceof WildcardType) {
        type = ((WildcardType) type).getUpperBounds()[0];
      } else if (type instanceof TypeVariable<?>) {
        type = ((TypeVariable<?>) type).getBounds()[0];
      } else if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        if (types == null || types.length == 0) {
          return Object.class;
        }
        if (types.length > 1) {
          throw new RuntimeException(clazz.getName()
              + "继承的泛型" + parameterizedType + "的实参数量多于1个");
        }
        type = parameterizedType.getActualTypeArguments()[0];
      } else if (type instanceof GenericArrayType) {
        type = ((GenericArrayType) type).getGenericComponentType();
      }
    }
    return (Class) type;
  }

  /**
   * 获取Field
   *
   * @param type   类型
   * @param filter 过滤器
   * @return 返回获取的Field map
   */
  public static Map<String, Field> getFields(Class<?> type, Predicate<Field> filter) {
    final Map<String, Field> fields = new LinkedHashMap<>();
    foreachField(type
        , filter
        , f -> fields.putIfAbsent(f.getName(), f)
        , f -> false);
    return fields;
  }

  /**
   * 迭代Class
   *
   * @param type        类
   * @param call        回调
   * @param filter      过滤器
   * @param handler     处理器
   * @param interceptor 拦截器
   * @param superclass  是否调用 getSuperclass()
   */
  public static <T> void foreach(Class<?> type,
                                 Function<Class<?>, T[]> call,
                                 Predicate<T> filter,
                                 Consumer<T> handler,
                                 Predicate<T> interceptor,
                                 boolean superclass) {

    if (type == null || type == Object.class) {
      return;
    }

    T[] ts = call.apply(type);
    for (T t : ts) {
      if (filter != null) {
        if (filter.test(t)) {
          handler.accept(t);
        }
      } else {
        handler.accept(t);
      }
      if (interceptor.test(t)) {
        return;
      }
    }

    if (superclass) {
      foreach(type.getSuperclass(), call, filter, handler, interceptor, true);
    }
  }

  /**
   * 是否被static 和 final 修饰
   *
   * @param modifiers 修饰符
   * @return 返回结果
   */
  public static boolean isStaticOrFinal(int modifiers) {
    return Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers);
  }

  /**
   * 获取某个字段
   *
   * @param type  类型
   * @param field 字段
   * @return 返回获取的字段对象
   */
  public static Field getField(Class<?> type, String field) {
    if (isNonNull(type, field) && !field.isEmpty() && type != Object.class) {
      try {
        return type.getDeclaredField(field);
      } catch (NoSuchFieldException e) {/* ignore */}
      return getField(type.getSuperclass(), field);
    }
    return null;
  }

  /**
   * 获取存储类字段的Map
   *
   * @param type        类
   * @param filter      过滤器
   * @param handler     处理器
   * @param interceptor 拦截器
   */
  public static void foreachField(Class<?> type,
                                  Predicate<Field> filter,
                                  Consumer<Field> handler,
                                  Predicate<Field> interceptor) {
    foreach(type, Class::getDeclaredFields, filter, handler, interceptor, true);
  }

  /**
   * 给对象中某个字段设置值
   *
   * @param o     对象
   * @param field 字段
   * @param value 值
   * @param <T>   类型
   */
  public static <T> void setFieldValue(T o, Field field, Object value) {
    if (field != null && o != null) {
      setAccessible(field, true);
      try {
        field.set(o, value);
      } catch (IllegalAccessException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  /**
   * 获取对象某字段的值
   *
   * @param field 字段对象
   * @param o     对象
   * @return 返回获取的值
   */
  public static <T> T getFieldValue(Field field, Object o, T defaultValue) {
    if (isNonNull(o, field)) {
      setAccessible(field, true);
      try {
        Object value = field.get(o);
        return value != null ? (T)value : defaultValue;
      } catch (IllegalAccessException ignore) { /* ~ */ }
    }
    return defaultValue;
  }

  /**
   * 设置是否可以访问
   *
   * @param ao   可访问对象
   * @param flag 是否可以访问
   */
  public static void setAccessible(AccessibleObject ao, boolean flag) {
    if (ao != null) {
      ao.setAccessible(flag);
    }
  }

  private static boolean isNonNull(Object... os) {
    for (Object o : os) {
      if (o == null) {
        return false;
      }
    }
    return true;
  }

}
