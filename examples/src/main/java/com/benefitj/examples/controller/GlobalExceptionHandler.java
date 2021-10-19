package com.benefitj.examples.controller;

import com.benefitj.spring.aop.ratelimiter.RateLimiterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {


  @ExceptionHandler(value = {RateLimiterException.class})
  public ResponseEntity<?> onRateLimiter(HttpServletRequest req, RateLimiterException e) {
    return ResponseEntity.ok(e.getMessage());
  }

}
