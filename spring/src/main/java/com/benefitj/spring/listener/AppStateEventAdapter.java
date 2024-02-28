package com.benefitj.spring.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

public class AppStateEventAdapter {

  static final Logger log = LoggerFactory.getLogger(AppStateEventAdapter.class);

  private List<AppStateListener> listeners;

  public AppStateEventAdapter(List<AppStateListener> listeners) {
    this.listeners = listeners;
  }

  /**
   * 程序启动
   */
  @EventListener(ApplicationReadyEvent.class)
  public void onAppStart(ApplicationReadyEvent evt) {
    processAppStart(AppStateHook.get().listeners(), evt);
    processAppStart(listeners, evt);
  }

  protected void processAppStart(List<AppStateListener> listeners, ApplicationReadyEvent evt) {
    if (listeners != null && !listeners.isEmpty()) {
      for (AppStateListener l : listeners) {
        try {
          l.onAppStart(evt);
        } catch (Exception e) {
          log.error("throws: " + e.getMessage(), e);
        }
      }
    }
  }

  /**
   * 程序结束
   */
  @EventListener(ContextClosedEvent.class)
  public void onAppStop(ContextClosedEvent evt) {
    processAppStop(AppStateHook.get().listeners(), evt);
    processAppStop(listeners, evt);
  }

  protected void processAppStop(List<AppStateListener> listeners, ContextClosedEvent event) {
    if (listeners != null && !listeners.isEmpty()) {
      for (AppStateListener l : listeners) {
        try {
          l.onAppStop(event);
        } catch (Exception e) {
          log.error("throws: " + e.getMessage(), e);
        }
      }
    }
  }

}
