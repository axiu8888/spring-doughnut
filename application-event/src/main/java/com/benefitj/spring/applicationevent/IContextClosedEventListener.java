package com.benefitj.spring.applicationevent;

import org.springframework.context.event.ContextClosedEvent;

/**
 * 应用关闭监听
 */
public interface IContextClosedEventListener {

  /**
   * 应用关闭
   *
   * @param event 事件
   */
  void onContextClosedEvent(ContextClosedEvent event);

}
