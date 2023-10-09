package com.benefitj.spring.aop.log;

import com.benefitj.spring.aop.AopAdvice;
import com.benefitj.spring.aop.web.WebPointCutHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 打印请求日志
 */
@Order
@ConditionalOnMissingBean({HttpLoggingHandler.class, HttpLoggingCustomizer.class})
@Component
public class HttpLoggingHandler implements WebPointCutHandler {

  /**
   * 打印的参数
   */
  private static final ThreadLocal<Map<String, Object>> printArgs = ThreadLocal.withInitial(LinkedHashMap::new);

  /**
   * 自定义处理
   */
  private HttpLoggingCustomizer httpLoggingCustomizer = HttpLoggingCustomizer.newCustomizer();

  public HttpLoggingHandler() {
  }

  public HttpLoggingCustomizer getHttpLoggingCustomizer() {
    return httpLoggingCustomizer;
  }

  @Autowired(required = false)
  public void setHttpLoggingCustomizer(HttpLoggingCustomizer httpLoggingCustomizer) {
    this.httpLoggingCustomizer = httpLoggingCustomizer;
  }

  public boolean support(JoinPoint point) {
    Method method = getMethod(point);
    return method.isAnnotationPresent(HttpLoggingIgnore.class)
        || method.getDeclaringClass().isAnnotationPresent(HttpLoggingIgnore.class);
  }

  @Override
  public void doBefore(AopAdvice advice, JoinPoint point) {
    if (support(point)) {
      return;
    }
    HttpLoggingCustomizer hlc = getHttpLoggingCustomizer();
    if (hlc.printable()) {
      ServletRequestAttributes attrs = getRequestAttributes();
      if (attrs != null) {
        try {
          Map<String, Object> args = getPrintArgs();
          Method method = checkProxy(((MethodSignature) point.getSignature()).getMethod(), point.getTarget());
          fillPrintArgs(point, method, attrs, args);
          hlc.customize(this, args);
        } finally {
          getPrintArgsLocal().remove();
        }
      }
    }
  }

  public void fillPrintArgs(JoinPoint point, Method method, ServletRequestAttributes attrs, Map<String, Object> argsMap) {
    HttpServletRequest request = attrs.getRequest();
    argsMap.put(request.getMethod(), request.getRequestURI());
    argsMap.put("class", method.getDeclaringClass().getSimpleName() + "." + method.getName());
    argsMap.put("args", mapToArgs(method.getParameters(), point.getArgs()));
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
        } else if (arg instanceof MultipartFile[]) {
          argsMap.put(parameter.getName(), "MultipartFiles[" + Arrays.stream(((MultipartFile[]) arg))
              .map(mf -> String.format("(%s, %d)", mf.getOriginalFilename(), mf.getSize()))
              .collect(Collectors.joining(", ")) + "]");
        } else if (arg instanceof InputStream) {
          argsMap.put(parameter.getName(), "[input]");
        } else if (arg instanceof OutputStream) {
          argsMap.put(parameter.getName(), "[output]");
        } else {
          argsMap.put(parameter.getName(), arg);
        }
      }
      return argsMap;
    }
    return null;
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

  public static void clearArgs() {
    getPrintArgs().clear();
  }

}
