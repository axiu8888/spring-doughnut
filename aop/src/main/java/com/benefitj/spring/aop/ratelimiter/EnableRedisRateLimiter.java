package com.benefitj.spring.aop.ratelimiter;


import com.benefitj.spring.aop.web.EnableAutoAopWebHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@EnableAutoAopWebHandler
@Import({RateLimiterHandler.class, RedisRateLimiterCustomizer.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableRedisRateLimiter {
}
