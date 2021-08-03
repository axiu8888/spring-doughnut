package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

/**
 * AOP切入点处理：前置/后置/异常/返回
 */
public interface PointCutHandler {

  default void doBefore(AbstractAspect aspect, JoinPoint joinPoint) {
    doBefore(joinPoint);
  }

  default void doBefore(JoinPoint joinPoint) {
  }

  default void doAfter(AbstractAspect aspect, JoinPoint joinPoint) {
    doAfter(joinPoint);
  }

  default void doAfter(JoinPoint joinPoint) {
  }

  default void doAfterThrowing(AbstractAspect aspect, JoinPoint joinPoint, Throwable ex) {
    doAfterThrowing(joinPoint, ex);
  }

  default void doAfterThrowing(JoinPoint joinPoint, Throwable ex) {
  }

  default void doAfterReturning(AbstractAspect aspect, JoinPoint joinPoint, Object returnValue) {
    doAfterReturning(joinPoint, returnValue);
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
