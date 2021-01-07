package com.benefitj.spring.aop;

import com.benefitj.spring.aop.web.WebRequestAspect;
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

  default Method checkProxy(Method methodArg, Object bean) {
    return WebRequestAspect.checkProxy(methodArg, bean);
  }
}
