package com.benefitj.spring.applicationevent;

import org.springframework.boot.context.event.ApplicationPreparedEvent;

/**
 * 初始化完成监听
 */
public interface IApplicationPreparedEventListener {

  /**
   * 初始化完成
   *
   * @param event 事件
   */
  void onApplicationPreparedEvent(ApplicationPreparedEvent event);

}
