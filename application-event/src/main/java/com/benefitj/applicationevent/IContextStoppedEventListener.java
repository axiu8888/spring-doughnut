package com.benefitj.applicationevent;

import org.springframework.context.event.ContextStoppedEvent;

/**
 * 应用停止监听
 */
public interface IContextStoppedEventListener {

  /**
   * 应用停止
   *
   * @param event 事件
   */
  void onContextStoppedEvent(ContextStoppedEvent event);
}
