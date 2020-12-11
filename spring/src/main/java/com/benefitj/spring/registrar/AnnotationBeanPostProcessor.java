package com.benefitj.spring.registrar;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * bean后置处理器
 */
public interface AnnotationBeanPostProcessor extends BeanPostProcessor, Ordered, BeanFactoryAware, SmartInitializingSingleton {

  @Override
  void setBeanFactory(BeanFactory beanFactory) throws BeansException;

  @Override
  void afterSingletonsInstantiated();

  @Override
  Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

  @Override
  Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

  @Override
  default int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  /**
   * 查找注解
   *
   * @param element        元素(class/field/method)
   * @param annotationType 注解类型
   * @param <A>            注解
   * @return 返回查找到的注解
   */
  default <A extends Annotation> Collection<A> findAnnotations(AnnotatedElement element, Class<A> annotationType) {
    return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
        .stream(annotationType)
        .map(MergedAnnotation::synthesize)
        .collect(Collectors.toList());
  }

  /**
   * 查找method集合
   *
   * @param targetClass     目标对象
   * @param annotationTypes 注解
   * @return 返回被注解注释的method集合
   */
  default Collection<Method> findAnnotationMethods(Class<?> targetClass, Class<? extends Annotation>... annotationTypes) {
    final List<Method> methods = new ArrayList<>();
    ReflectionUtils.MethodFilter filter = ReflectionUtils.USER_DECLARED_METHODS;
    ReflectionUtils.doWithMethods(targetClass, methods::add
        , method -> filter.matches(method) && isAnnotationPresent(method, annotationTypes));
    return methods;
  }

  /**
   * 判断元素是否被注解注释
   *
   * @param element         元素
   * @param annotationTypes 注解数组
   * @return 返回是否被注解注释
   */
  default boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation>[] annotationTypes) {
    if (annotationTypes != null && annotationTypes.length > 0) {
      for (Class<? extends Annotation> annotationType : annotationTypes) {
        if (!element.isAnnotationPresent(annotationType)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * 检查是否动态代理的方法
   *
   * @param methodArg 方法
   * @param bean      bean对象
   * @return 返回原始的method
   */
  default Method checkProxy(Method methodArg, Object bean) {
    Method method = methodArg;
    if (AopUtils.isJdkDynamicProxy(bean)) {
      try {
        // Found a @RabbitListener method on the target class for this JDK proxy ->
        // is it also present on the proxy itself?
        method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
        Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
        for (Class<?> iface : proxiedInterfaces) {
          try {
            method = iface.getMethod(method.getName(), method.getParameterTypes());
            break;
          }
          catch (@SuppressWarnings("unused") NoSuchMethodException noMethod) {
          }
        }
      }
      catch (SecurityException ex) {
        ReflectionUtils.handleReflectionException(ex);
      }
      catch (NoSuchMethodException ex) {
        throw new IllegalStateException(String.format(
            "method '%s' found on bean target class '%s', " +
                "but not found in any interface(s) for a bean JDK proxy. Either " +
                "pull the method up to an interface or switch to subclass (CGLIB) " +
                "proxies by setting proxy-target-class/proxyTargetClass " +
                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
      }
    }
    return method;
  }

}
