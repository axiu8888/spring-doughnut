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
   * @param advice 通知
   * @param point  切入点
   */
  default void doBefore(AopAdvice advice, JoinPoint point) {
  }

  /**
   * 调用方法后
   *
   * @param advice      通知
   * @param point       切入点
   * @param returnValue 返回值的引用
   */
  default void doAfter(AopAdvice advice, JoinPoint point, AtomicReference<Object> returnValue) {
  }

  /**
   * 调用方法时抛出异常
   *
   * @param advice 通知
   * @param point  切入点
   * @param ex     异常
   */
  default void doThrowing(AopAdvice advice, JoinPoint point, Throwable ex) {
  }

  /**
   * 最终返回结果
   *
   * @param advice 通知
   * @param point  切入点
   */
  default void doAfterReturning(AopAdvice advice, JoinPoint point) {
  }

  /**
   * 是否拦截，默认false
   *
   * @param advice         通知
   * @param point          切入点
   * @param returnValueRef 返回值得引用
   * @return 返回是否拦截
   */
  default boolean isInterceptor(AopAdvice advice, JoinPoint point, AtomicReference<Object> returnValueRef) {
    return false;
  }

  default Method getMethod(JoinPoint point) {
    return AopUtils.getMethod(point);
  }

  default Class<?> getDeclaringType(JoinPoint point) {
    return AopUtils.getDeclaringType(point);
  }

  default Method checkProxy(Method methodArg, Object bean) {
    return AopUtils.checkProxy(methodArg, bean);
  }

}
