package com.benefitj.applicationevent;

import org.springframework.context.ApplicationEvent;

/**
 * 其他事件监听
 */
public interface IOtherApplicationEventListener {

  /**
   * 其他事件
   *
   * @param event 事件
   */
  void onOtherApplicationEvent(ApplicationEvent event);
}
