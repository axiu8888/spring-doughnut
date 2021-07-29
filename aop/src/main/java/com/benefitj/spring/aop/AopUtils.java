package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.Advised;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class AopUtils {

  /**
   * 获取声明类型
   *
   * @param jp 切入点
   * @return 返回声明类型
   */
  public static Class<?> getDeclaringType(JoinPoint jp) {
    return jp.getSignature().getDeclaringType();
  }

  /**
   * 获取方法
   *
   * @param jp 切入点
   * @return 返回方法
   */
  public static Method getMethod(JoinPoint jp) {
    return ((MethodSignature) jp.getSignature()).getMethod();
  }


  public static Method checkProxy(Method methodArg, Object bean) {
    Method method = methodArg;
    if (org.springframework.aop.support.AopUtils.isJdkDynamicProxy(bean)) {
      try {
        // Found a @RabbitListener method on the target class for this JDK proxy ->
        // is it also present on the proxy itself?
        method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
        Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
        for (Class<?> iface : proxiedInterfaces) {
          try {
            method = iface.getMethod(method.getName(), method.getParameterTypes());
            break;
          } catch (@SuppressWarnings("unused") NoSuchMethodException noMethod) {
          }
        }
      } catch (SecurityException ex) {
        ReflectionUtils.handleReflectionException(ex);
      } catch (NoSuchMethodException ex) {
        throw new IllegalStateException(String.format(
            "WebRequestAspect method '%s' found on bean target class '%s', " +
                "but not found in any interface(s) for a bean JDK proxy. Either " +
                "pull the method up to an interface or switch to subclass (CGLIB) " +
                "proxies by setting proxy-target-class/proxyTargetClass " +
                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
      }
    }
    return method;
  }

}
