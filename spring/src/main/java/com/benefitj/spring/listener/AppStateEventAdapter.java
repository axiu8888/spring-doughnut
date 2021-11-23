package com.benefitj.spring.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

public class AppStateEventAdapter {

  private static final Logger log = LoggerFactory.getLogger(AppStateEventAdapter.class);

  private AppStateHook registrar = AppStateHook.getInstance();

  private List<AppStateListener> listeners;

  public AppStateEventAdapter(List<AppStateListener> listeners) {
    this.listeners = listeners;
  }

  /**
   * 程序启动
   */
  @EventListener
  public void onAppStart(ApplicationReadyEvent event) {
    processAppStart(registrar.listeners(), event);
    processAppStart(listeners, event);
  }

  protected void processAppStart(List<AppStateListener> listeners, ApplicationReadyEvent event) {
    if (listeners != null && !listeners.isEmpty()) {
      for (AppStateListener l : listeners) {
        try {
          l.onAppStart(event);
        } catch (Exception e) {
          log.error("throws: " + e.getMessage(), e);
        }
      }
    }
  }

  /**
   * 程序结束
   */
  @EventListener
  public void onAppStop(ContextClosedEvent event) {
    processAppStop(registrar.listeners(), event);
    processAppStop(listeners, event);
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
