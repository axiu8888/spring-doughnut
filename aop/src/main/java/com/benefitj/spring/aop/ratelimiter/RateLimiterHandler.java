package com.benefitj.spring.aop.ratelimiter;

import com.benefitj.core.local.LocalCacheFactory;
import com.benefitj.core.local.LocalMapCache;
import com.benefitj.spring.aop.AopAdvice;
import com.benefitj.spring.aop.AopUtils;
import com.benefitj.spring.aop.web.WebPointCutHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@ConditionalOnMissingBean(RateLimiterHandler.class)
@Component
public class RateLimiterHandler implements WebPointCutHandler {

  private final LocalMapCache<Method, AopRateLimiter> cache;

  private RateLimiterCustomizer customizer;

  public RateLimiterHandler(@Autowired RateLimiterCustomizer customizer) {
    this.customizer = customizer;
    this.cache = LocalCacheFactory.newConcurrentHashMapCache();
    this.cache.setAbsentFunction(method -> AopUtils.findAnnotation(method, AopRateLimiter.class));
  }

  @Override
  public void doBefore(AopAdvice advice, JoinPoint joinPoint) {
    Method method = checkProxy(((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getTarget());
    AopRateLimiter rateLimiter = cache.computeIfAbsent(method);
    if (rateLimiter != null) {
      getCustomizer().customize(joinPoint, method, rateLimiter);
    }
  }

  public RateLimiterCustomizer getCustomizer() {
    return customizer;
  }

  public void setCustomizer(RateLimiterCustomizer customizer) {
    this.customizer = customizer;
  }

}
