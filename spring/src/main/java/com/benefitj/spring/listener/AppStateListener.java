package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;

import java.util.function.Consumer;

/**
 * APP启动和停止的监听
 */
public interface AppStateListener {

  static AppStateListener create(Consumer<ApplicationReadyEvent> starter, Consumer<ContextClosedEvent> stopper) {
    return new AppStateListener() {
      @Override
      public void onAppStart(ApplicationReadyEvent event) throws Exception {
        starter.accept(event);
      }

      @Override
      public void onAppStop(ContextClosedEvent event) throws Exception {
        stopper.accept(event);
      }
    };
  }

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
