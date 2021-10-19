package com.benefitj.spring.aop.ratelimiter;

import com.benefitj.spring.aop.web.WebRequestHolder;
import org.aspectj.lang.JoinPoint;

import java.lang.reflect.Method;

public interface RateLimiterCustomizer extends WebRequestHolder {

  /**
   * 处理请求日志
   *
   * @param joinPoint   切入点
   * @param method      方法
   * @param rateLimiter 注解
   */
  void customize(JoinPoint joinPoint, Method method, AopRateLimiter rateLimiter);

}
