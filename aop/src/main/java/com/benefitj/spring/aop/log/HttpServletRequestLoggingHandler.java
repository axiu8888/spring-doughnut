package com.benefitj.spring.aop.log;

import com.benefitj.spring.aop.WebPointCutHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 打印请求日志
 */
@Order(100)
@Component
public class HttpServletRequestLoggingHandler implements WebPointCutHandler {

  private static final Logger log = LoggerFactory.getLogger(HttpServletRequestLoggingHandler.class);

  private final ObjectMapper mapper = new ObjectMapper();
  /**
   * 是否打印日志
   */
  @Value("#{ @environment['com.benefitj.aop.log.print'] ?: true }")
  private boolean print = true;
  /**
   * 是否分多行打印
   */
  @Value("#{ @environment['com.benefitj.aop.log.multi-line'] ?: true }")
  private boolean multiLine = true;

  /**
   * 打印的参数
   */
  private static final ThreadLocal<Map<String, Object>> printArgs = ThreadLocal.withInitial(LinkedHashMap::new);

  public HttpServletRequestLoggingHandler() {
  }

  @Override
  public void doBefore(JoinPoint joinPoint) {
    if (!isPrint()) {
      return;
    }
    ServletRequestAttributes attrs = getRequestAttributes();
    if (attrs != null) {
      try {
        Map<String, Object> argsMap = getPrintArgs();
        ProceedingJoinPoint point = (ProceedingJoinPoint) joinPoint;
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        fillPrintArgs(point, method, attrs, argsMap);
        printLog(argsMap);
      } finally {
        getPrintArgsLocal().remove();
      }
    }
  }

  public void fillPrintArgs(ProceedingJoinPoint point, Method method, ServletRequestAttributes attrs, Map<String, Object> argsMap) {
    HttpServletRequest request = attrs.getRequest();
    argsMap.put("uri", request.getRequestURI());
    argsMap.put("request method", request.getMethod());
    argsMap.put("class", method.getDeclaringClass().getName());
    argsMap.put("class method", method.getName());
    argsMap.put("class method args", mapToArgs(method.getParameters(), point.getArgs()));
  }

  public Map<String, Object> mapToArgs(Parameter[] parameters, Object[] args) {
    if (parameters != null && parameters.length > 0) {
      final Map<String, Object> argsMap = new LinkedHashMap<>();
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
        } else {
          argsMap.put(parameter.getName(), arg);
        }
      }
      return argsMap;
    }
    return null;
  }


  public void printLog(Map<String, Object> argsMap) {
    final StringBuilder sb = new StringBuilder();
    sb.append(isMultiLine() ? "\n" : "");
    String separator = separator();
    argsMap.forEach((key, value) ->
        sb.append(key).append(": ").append(toJson(value)).append(separator));
    sb.replace(sb.length() - separator.length(), sb.length(), "");
    log.info(sb.toString());
  }

  private String separator() {
    return isMultiLine() ? "\n" : ", ";
  }

  public String toJson(Object o) {
    if (o == null) {
      return "";
    }
    try {
      if (o instanceof Number
          || o instanceof Boolean
          || o instanceof CharSequence
          || o instanceof Character) {
        return String.valueOf(o);
      }
      return getMapper().writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  public ObjectMapper getMapper() {
    return mapper;
  }

  public boolean isPrint() {
    return print;
  }

  public void setPrint(boolean print) {
    this.print = print;
  }

  public boolean isMultiLine() {
    return multiLine;
  }

  public void setMultiLine(boolean multiLine) {
    this.multiLine = multiLine;
  }

  public static ThreadLocal<Map<String, Object>> getPrintArgsLocal() {
    return printArgs;
  }

  public static Map<String, Object> getPrintArgs() {
    return getPrintArgsLocal().get();
  }

  public static void putArg(String key, Object value) {
    getPrintArgs().put(key, value);
  }

  public static Object removeArg(String key) {
    return getPrintArgs().remove(key);
  }

  public static void putArgs(Map<String, Object> args) {
    getPrintArgs().putAll(args);
  }

}
