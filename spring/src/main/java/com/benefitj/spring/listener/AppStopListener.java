package com.benefitj.spring.listener;

import org.springframework.context.event.ContextClosedEvent;

public interface AppStopListener {

  /**
   * 程序停止
   *
   * @param event
   */
  void onAppStop(ContextClosedEvent event);

}
