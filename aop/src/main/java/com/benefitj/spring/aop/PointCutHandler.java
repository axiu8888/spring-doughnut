package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * AOP切入点处理：前置/后置/异常/返回
 */
public interface PointCutHandler {
  /**
   * 调用方法前
   *
   * @param advice    通知
   * @param joinPoint 切入点
   */
  default void doBefore(AopAdvice advice, JoinPoint joinPoint) {
  }

  /**
   * 调用方法后
   *
   * @param advice      通知
   * @param joinPoint   切入点
   * @param returnValue 返回值的引用
   */
  default void doAfter(AopAdvice advice, JoinPoint joinPoint, AtomicReference<Object> returnValue) {
  }

  /**
   * 调用方法时抛出异常
   *
   * @param advice    通知
   * @param joinPoint 切入点
   * @param ex        异常
   */
  default void doAfterThrowing(AopAdvice advice, JoinPoint joinPoint, Throwable ex) {
  }

  /**
   * 最终返回结果
   *
   * @param advice    通知
   * @param joinPoint 切入点
   */
  default void doAfterReturning(AopAdvice advice, JoinPoint joinPoint) {
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
