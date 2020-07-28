package com.benefitj.spring.applicationevent;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;

/**
 * 初始化环境变量监听
 */
public interface IApplicationEnvironmentPreparedEventListener {

  /**
   * 初始化环境变量
   *
   * @param event 事件
   */
  void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event);

}
