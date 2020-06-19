package com.benefitj.applicationevent;

import org.springframework.boot.context.event.ApplicationStartedEvent;

/**
 * 应用启动中监听
 */
public interface IApplicationStartedEventListener {

  /**
   * 应用启动中
   *
   * @param event 事件
   */
  void onApplicationStartedEvent(ApplicationStartedEvent event);
}
