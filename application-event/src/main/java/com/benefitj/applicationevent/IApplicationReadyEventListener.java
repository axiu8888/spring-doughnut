package com.benefitj.applicationevent;

import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * 应用已启动完成
 */
public interface IApplicationReadyEventListener {

  /**
   * 应用已启动完成
   *
   * @param event 事件
   */
  void onApplicationReadyEvent(ApplicationReadyEvent event);
}
