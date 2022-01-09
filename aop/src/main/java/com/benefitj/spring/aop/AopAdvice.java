package com.benefitj.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 通知
 */
public interface AopAdvice {

  /**
   * 执行之前
   *
   * @param point    连接点
   * @param handlers 处理器
   */
  default void doBefore(ProceedingJoinPoint point, List<PointCutHandler> handlers) {
    handlers.forEach(h -> h.doBefore(this, point));
  }

  /**
   * 执行之后
   *
   * @param point    连接点
   * @param handlers 处理器
   */
  default void doAfter(ProceedingJoinPoint point, AtomicReference<Object> returnValue, List<PointCutHandler> handlers) {
    handlers.forEach(h -> h.doAfter(this, point, returnValue));
  }

  /**
   * 抛出异常时
   *
   * @param point    连接点
   * @param e        异常
   * @param handlers 处理器
   */
  default void doAfterThrowing(ProceedingJoinPoint point, Throwable e, List<PointCutHandler> handlers) {
    handlers.forEach(h -> h.doAfterThrowing(this, point, e));
  }

  /**
   * 执行之后，返回值时
   *
   * @param point    连接点
   * @param handlers 处理器
   */
  default void doAfterReturning(ProceedingJoinPoint point, List<PointCutHandler> handlers) {
    handlers.forEach(h -> h.doAfterReturning(this, point));
  }

  /**
   * 注册 Handler
   */
  void register(PointCutHandler handlers);

}
