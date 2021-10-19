package com.benefitj.spring.aop.ratelimiter;

public class RateLimiterException extends RuntimeException {

  public RateLimiterException() {
  }

  public RateLimiterException(String message) {
    super(message);
  }
}
