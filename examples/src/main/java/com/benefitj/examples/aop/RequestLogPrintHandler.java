package com.benefitj.examples.aop;

import com.alibaba.fastjson.JSON;
import com.benefitj.aop.WebPointCutHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 打印请求日志
 */
@Order(100)
@Component
public class RequestLogPrintHandler implements WebPointCutHandler {

  private static final Logger log = LoggerFactory.getLogger(RequestLogPrintHandler.class);

  @Override
  public void doBefore(JoinPoint joinPoint) {
    ServletRequestAttributes attrs = getRequestAttributes();
    if (attrs != null) {
      ProceedingJoinPoint point = (ProceedingJoinPoint) joinPoint;
      Method method = ((MethodSignature) point.getSignature()).getMethod();
      final Map<String, Object> argsMap = new LinkedHashMap<>();
      Object[] args = point.getArgs();
      Parameter[] parameters = method.getParameters();
      for (int i = 0; i < parameters.length; i++) {
        Object arg = args[i];
        Parameter parameter = parameters[i];
        if (arg instanceof ServletRequest) {
          argsMap.put(parameter.getName(), "[ServletRequest]");
        } else if (arg instanceof ServletResponse) {
          argsMap.put(parameter.getName(), "[ServletResponse]");
        } else if (arg instanceof MultipartFile) {
          MultipartFile mf = (MultipartFile) arg;
          argsMap.put(parameter.getName(), String.format("[MultipartFile(%s, %d)]"
              , mf.getOriginalFilename(), mf.getSize()));
        } else if (arg instanceof InputStream) {
          argsMap.put(parameter.getName(), "[InputStream]");
        } else if (arg instanceof OutputStream) {
          argsMap.put(parameter.getName(), "[OutputStream]");
        } else {
          argsMap.put(parameter.getName(), arg);
        }
      }
      HttpServletRequest request = attrs.getRequest();
      log.info("uri: {}, method: {}, params: {}",
          request.getRequestURI(), request.getMethod(), JSON.toJSONString(argsMap));
    }
  }
}
