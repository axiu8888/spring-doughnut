package com.benefitj.system.controller.exception;

import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.system.service.SysException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

  /**
   * 文件过大
   */
  @ExceptionHandler(value = {MultipartException.class})
  public HttpResult<?> httpRequestMethodNotSupported(HttpServletRequest req,
                                                     MultipartException e) {
    log.error("[{}]请求出错: {}", req.getRequestURI(), e.getMessage());
    return HttpResult.fail(400, e.getMessage());
  }

  /**
   * 请求方法不支持
   */
  @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
  public HttpResult<?> httpRequestMethodNotSupported(HttpServletRequest req,
                                                     HttpRequestMethodNotSupportedException e) {
    log.error("[{}]请求出错: {}", req.getRequestURI(), e.getMessage());
    return HttpResult.fail(400, e.getMessage());
  }

  /**
   * JWT过期的异常
   */
  @ExceptionHandler(value = ExpiredJwtException.class)
  public HttpResult expiredJwtExceptionHandler(HttpServletRequest req, ExpiredJwtException e) {
    log.error("[{}]请求出错: {}", req.getRequestURI(), e.getMessage(), e);
    return HttpResult.fail(403, "Authorization failure!");
  }

  /**
   * 客户端被中断的异常，比如原本弹出的是浏览器的下载，然后被改为了迅雷下载
   */
  @ExceptionHandler(value = ClientAbortException.class)
  public HttpResult clientAbortExceptionHandler(HttpServletRequest req, ClientAbortException e) {
    log.error("[" + req.getRequestURI() + "]请求出错: ", e);
    if (StringUtils.isBlank(e.getMessage())) {
      return HttpResult.fail(500, "服务器错误");
    }
    return HttpResult.fail(500, e.getMessage());
  }

  @ExceptionHandler(value = {IllegalStateException.class, IllegalArgumentException.class, SysException.class})
  public HttpResult simpleExceptionHandler(HttpServletRequest req, Throwable e) {
    log.error("[{}] 请求出错: {}, e.getClass(): {}", req.getRequestURI(), e.getMessage(), e.getClass());
    //e.printStackTrace();
    if (StringUtils.isBlank(e.getMessage())) {
      e.printStackTrace();
      return HttpResult.fail(500, "服务器错误");
    }
    return HttpResult.fail(400, e.getMessage());
  }

  @ExceptionHandler(value = Throwable.class)
  public HttpResult defaultHandler(HttpServletRequest req, Throwable e) {
    log.error("[" + req.getRequestURI() + "]请求出错: " + e.getMessage(), e);
    if (StringUtils.isBlank(e.getMessage())) {
      e.printStackTrace();
      return HttpResult.fail(500, "服务器错误");
    }
    return HttpResult.fail(400, e.getMessage());
  }

}
