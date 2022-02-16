package com.benefitj.spring.freemarker.jdbc;

import com.benefitj.core.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.WeakHashMap;

public class AnnotationConverterProxy implements AnnotationConverter<Annotation> {

  private final Map<Class<?>, Annotation> annotationCache = new WeakHashMap<>();

  @Override
  public String convert(Annotation annotation) {
    Annotation annotationProxy = annotationCache.computeIfAbsent(annotation.getClass()
        , type -> (Annotation) Proxy.newProxyInstance(type.getClassLoader()
            , new Class[]{type}
            , (proxy, method, args) -> method.isDefault() ? null : ReflectUtils.invoke(annotation, method, args))
    );

    StringBuilder sb = new StringBuilder();
    ReflectUtils.findMethods(annotation.getClass()
        , m -> true
        , m -> {
          Object value = ReflectUtils.invoke(annotationProxy, m);
          if (value != null) {
            sb.append(m.getName()).append("=").append(valueConvert(value));
          }
        }
        , m -> false
    );
    return sb.toString();
  }

  public String valueConvert(Object value) {
    if (value instanceof String) {
      return "\"" + value + "\"";
    } else if (value instanceof Class) {
      return ((Class<?>) value).getName() + ".class";
    } else {
      return String.valueOf(value);
    }
  }

}
