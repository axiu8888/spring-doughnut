package com.benefitj.applicationevent;

import org.springframework.context.event.ContextStartedEvent;

/**
 * 应用启动监听
 */
public interface IContextStartedEventListener {
  /**
   * 应用启动
   *
   * @param event 事件
   */
  void onContextStartedEvent(ContextStartedEvent event);

}
