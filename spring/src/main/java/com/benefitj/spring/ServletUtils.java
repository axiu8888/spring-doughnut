package com.benefitj.spring;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servlet工具
 */
public class ServletUtils {


  @Nullable
  public static ServletRequestAttributes getRequestAttributes() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
  }

  /**
   * 获取当前请求的属性缓存，如果不存在，抛出 IllegalStateException 异常
   */
  public static ServletRequestAttributes currentRequestAttributes() throws IllegalStateException {
    return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
  }

  /**
   * 获取HTTP请求
   */
  public static HttpServletRequest getRequest() {
    return currentRequestAttributes().getRequest();
  }

  /**
   * 获取HTTP响应
   */
  public static HttpServletResponse getResponse() {
    return currentRequestAttributes().getResponse();
  }

  /**
   * 获取请求参数
   */
  public static Map<String, String[]> getParameterMap() {
    return getRequest().getParameterMap();
  }

  /**
   * 获取请求首部
   */
  public static String getHeader(String headerName) {
    return getRequest().getHeader(headerName);
  }

  /**
   * 获取请求首部
   */
  public static Map<String, String> getHeaderMap() {
    return getHeaderMap(getRequest());
  }

  /**
   * 获取请求首部
   */
  public static Map<String, String> getHeaderMap(HttpServletRequest request) {
    return enumerationToMap(request.getHeaderNames(), request::getHeader);
  }

  /**
   * 获取请求信息
   */
  public static Map<String, Object> getRequestInfo() {
    return getRequestInfo(getRequest());
  }

  /**
   * 获取请求信息
   */
  public static Map<String, Object> getRequestInfo(HttpServletRequest request) {
    Map<String, Object> infoMap = new LinkedHashMap<>();
    infoMap.put("remoteHost", request.getRemoteHost());
    infoMap.put("remotePort", request.getRemotePort());
    infoMap.put("contextPath", request.getContextPath());
    infoMap.put("servletPath", request.getServletPath());
    infoMap.put("characterEncoding", request.getCharacterEncoding());
    infoMap.put("contentLength", request.getContentLength());
    infoMap.put("dispatcherType", request.getDispatcherType());
    infoMap.put("requestURI", request.getRequestURI());
    infoMap.put("method", request.getMethod());
    infoMap.put("header", getHeaderMap(request));
    infoMap.put("queryString", request.getQueryString());
    infoMap.put("parameterMap", request.getParameterMap());
    infoMap.put("attributeNames", request.getAttributeNames());
    infoMap.put("pathInfo", request.getPathInfo());
    infoMap.put("authType", request.getAuthType());
    infoMap.put("cookies", request.getCookies());
    infoMap.put("remoteUser", request.getRemoteUser());
    infoMap.put("requestedSessionId", request.getRequestedSessionId());
    infoMap.put("trailerFields", request.getTrailerFields());
    infoMap.put("locale", request.getLocale());
    infoMap.put("locales", request.getLocales());
    return infoMap;
  }


  public static <T, R> Map<T, R> enumerationToMap(Enumeration<T> enumeration, Function<T, R> func) {
    Map<T, R> map = new LinkedHashMap<>();
    T t;
    R r;
    while (enumeration.hasMoreElements()) {
      t = enumeration.nextElement();
      r = func.apply(t);
      map.put(t, r);
    }
    return map;
  }

}
