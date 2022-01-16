package com.benefitj.system.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {

  private static final String UNKNOWN = "unknown";

  /**
   * 获取IP地址
   *
   * @param request 请求
   * @return 返回IP地址
   */
  public static String getIp(HttpServletRequest request) {
    String ip = request.getHeader("x_forward_for");
    if (isIllegalIp(ip)) {
      ip = request.getHeader("X-Forwarded-For");
    }
    if (StringUtils.isNotBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
      //多次反向代理后会有多个ip值，第一个ip才是真实ip
      int index = ip.indexOf(",");
      return index > 0 ? ip.substring(0, index) : ip;
    }

    if (isIllegalIp(ip)) {
      ip = request.getHeader("x-real_ip");
    }
    if (isIllegalIp(ip)) {
      ip = request.getHeader("X-Real-IP");
    }
    if (isIllegalIp(ip)) {
      ip = request.getHeader("X-Forwarded-For");
    }
    if (isIllegalIp(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (isIllegalIp(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (isIllegalIp(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }
    if (isIllegalIp(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (isIllegalIp(ip)) {
      ip = request.getRemoteHost();
    }
    return ip;
  }

  /**
   * 获取IP地址
   *
   * @param request 请求
   * @return 返回IP地址
   */
  public static String getIpAddress(HttpServletRequest request) {
    return getIp(request) + ":" + request.getRemotePort();
  }

  private static boolean isIllegalIp(String ip) {
    return StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip);
  }


}
