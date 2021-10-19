package com.benefitj.spring.aop.ratelimiter;

import com.benefitj.core.DateFmtter;
import com.benefitj.spring.ServletUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Redis缓存
 */
@ConditionalOnMissingBean(RateLimiterCustomizer.class)
@Component
public class RedisRateLimiterCustomizer implements RateLimiterCustomizer {

  @Autowired
  private RedisTemplate<String, Integer> redisTemplate;

  /**
   * 处理请求日志
   *
   * @param joinPoint   切入点
   * @param method      方法
   * @param rateLimiter 注解
   */
  @Override
  public void customize(JoinPoint joinPoint, Method method, AopRateLimiter rateLimiter) {
    if (rateLimiter.qps() > 0) {
      String key = keyOf(method, rateLimiter);
      Long count = getRedisTemplate().opsForValue().increment(key, 1);
      getRedisTemplate().expire(key, rateLimiter.timeout(), rateLimiter.timeoutUnit());
      if (count != null && count > rateLimiter.qps()) {
        throw new RateLimiterException("超过请求次数!");
      }
    }
  }

  public String keyOf(Method method, AopRateLimiter rateLimiter) {
    String timeoutKey;
    switch (rateLimiter.timeoutUnit()) {
      case HOURS:
        timeoutKey = DateFmtter.fmtNow("ddHH");
        break;
      case DAYS:
        timeoutKey = DateFmtter.fmtNow("MMdd");
        break;
      default:
        timeoutKey = DateFmtter.fmtNow("ddHHmm");
        break;
    }
    return String.format("%s:%s:%s:%s"
        , ServletUtils.getIp(getRequest())
        , method.getDeclaringClass().getSimpleName()
        , method.getName()
        , timeoutKey
    );
  }

  public RedisTemplate<String, Integer> getRedisTemplate() {
    return redisTemplate;
  }

}
