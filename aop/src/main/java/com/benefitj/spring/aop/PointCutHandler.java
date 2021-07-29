package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

/**
 * AOP切入点处理：前置/后置/异常/返回
 */
public interface PointCutHandler {

  default void doBefore(JoinPoint joinPoint) {
  }

  default void doAfter(JoinPoint joinPoint) {
  }

  default void doAfterThrowing(JoinPoint joinPoint, Throwable ex) {
  }

  default void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
  }

  default Method getMethod(JoinPoint jp) {
    return AopUtils.getMethod(jp);
  }

  default Class<?> getDeclaringType(JoinPoint jp) {
    return AopUtils.getDeclaringType(jp);
  }

  default Method checkProxy(Method methodArg, Object bean) {
    return AopUtils.checkProxy(methodArg, bean);
  }

}
