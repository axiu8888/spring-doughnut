package com.benefitj.applicationevent;

import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 应用刷新监听
 */
public interface IContextRefreshedEventListener {

  /**
   * 应用刷新
   *
   * @param event 事件
   */
  void onContextRefreshedEvent(ContextRefreshedEvent event);
}
