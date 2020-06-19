package com.benefitj.applicationevent;

import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * Servlet请求处理事件
 */
public interface IServletRequestHandledEventListener {

  /**
   * 请求事件
   *
   * @param event 事件
   */
  void onServletRequestHandledEvent(ServletRequestHandledEvent event);

}
