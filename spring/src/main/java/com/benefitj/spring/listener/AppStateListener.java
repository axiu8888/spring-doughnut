package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;

/**
 * APP启动和停止的监听
 */
public interface AppStateListener {

  /**
   * 程序启动
   *
   * @param event
   */
  default void onAppStart(ApplicationReadyEvent event) throws Exception {
    // ~
  }

  /**
   * 程序停止
   *
   * @param event
   */
  default void onAppStop(ContextClosedEvent event) throws Exception {
    // ~
  }

}
