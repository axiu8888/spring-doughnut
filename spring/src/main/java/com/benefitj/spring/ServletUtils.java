package com.benefitj.spring;

import org.apache.commons.lang3.StringUtils;
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
   * 获取当前请求的参数
   */
  public static Map<String, String[]> getParameterMap() {
    return getRequest().getParameterMap();
  }

  /**
   * 获取当前请求的首部
   */
  public static String getHeader(String headerName) {
    return getRequest().getHeader(headerName);
  }

  /**
   * 获取当前请求的首部
   */
  public static Map<String, String> getHeaderMap() {
    HttpServletRequest request = getRequest();
    return getHeaderMap(request);
  }

  /**
   * 获取请求首部
   */
  public static Map<String, String> getHeaderMap(HttpServletRequest request) {
    return enumerationToMap(request.getHeaderNames(), request::getHeader);
  }

  /**
   * 获取当前请求的IP地址
   *
   * @return 返回IP地址
   */
  public static String getIp() {
    HttpServletRequest request = getRequest();
    return getIp(request);
  }

  /**
   * 获取IP地址
   *
   * @param request 请求
   * @return 返回IP地址
   */
  public static String getIp(HttpServletRequest request) {
    String ip = request.getHeader("x_forward_for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      request.getHeader("X-Forwarded-For");
    }

    if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
      // 多次反向代理后会有多个ip值，第一个ip才是真实ip
      int index = ip.indexOf(",");
      return index > 0 ? ip.substring(0, index) : ip;
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("x-real_ip");
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Real-IP");
    }

    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("X-Forwarded-For");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }

    if (StringUtils.isBlank(ip)) {
      ip = request.getRemoteHost();
    }

    return ip;
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

  /**
   * 将Enumeration转换为Map
   *
   * @param enumeration e
   * @param func        处理函数
   * @param <K>         键
   * @param <V>         值
   * @return 返回 Map
   */
  public static <K, V> Map<K, V> enumerationToMap(Enumeration<K> enumeration, Function<K, V> func) {
    K k;
    V v;
    Map<K, V> map = new LinkedHashMap<>();
    while (enumeration.hasMoreElements()) {
      k = enumeration.nextElement();
      v = func.apply(k);
      map.put(k, v);
    }
    return map;
  }

}
