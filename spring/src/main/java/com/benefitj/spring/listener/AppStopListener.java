package com.benefitj.spring.listener;

import org.springframework.context.event.ContextClosedEvent;

/**
 * APP停止的监听
 */
public interface AppStopListener {

  /**
   * 程序停止
   *
   * @param event
   */
  void onAppStop(ContextClosedEvent event) throws Exception;

}
