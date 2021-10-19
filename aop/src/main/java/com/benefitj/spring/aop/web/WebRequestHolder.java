package com.benefitj.spring.aop.web;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebRequestHolder {

  @Nullable
  default ServletRequestAttributes getRequestAttributes() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
  }

  @Nullable
  default HttpServletRequest getRequest() {
    ServletRequestAttributes attrs = getRequestAttributes();
    return attrs != null ? attrs.getRequest() : null;
  }

  @Nullable
  default HttpServletResponse getResponse() {
    ServletRequestAttributes attrs = getRequestAttributes();
    return attrs != null ? attrs.getResponse() : null;
  }

}
