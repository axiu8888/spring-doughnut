package com.benefitj.spring.aop.web;

import com.benefitj.spring.aop.PointCutHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * AOP切入点处理：前置/后置/异常/返回
 */
public interface WebPointCutHandler extends PointCutHandler {

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

  default Method checkProxy(Method methodArg, Object bean) {
    return WebRequestAspect.checkProxy(methodArg, bean);
  }
}
