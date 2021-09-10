package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;

public interface AppStateListener {

  /**
   * 程序启动
   *
   * @param event
   */
  default void onAppStart(ApplicationReadyEvent event) {
    // ~
  }

  /**
   * 程序停止
   *
   * @param event
   */
  default void onAppStop(ContextClosedEvent event) {
    // ~
  }

}
