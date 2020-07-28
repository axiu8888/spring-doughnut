package com.benefitj.spring.applicationevent;

import org.springframework.boot.context.event.ApplicationStartingEvent;

/**
 * 应用启动中监听
 */
public interface IApplicationStartingEventListener {

  /**
   * 应用启动中
   *
   * @param event 事件
   */
  void onApplicationStartingEvent(ApplicationStartingEvent event);
}
