package com.benefitj.applicationevent;

import org.springframework.boot.context.event.ApplicationContextInitializedEvent;

/**
 * 上下文初始化的监听
 */
public interface IApplicationContextInitializedEventListener {

  /**
   * 上下文初始化时
   *
   * @param event 事件
   */
  void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event);

}
