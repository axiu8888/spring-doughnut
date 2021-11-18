package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * App启动完成的监听
 */
public interface AppStartListener {

  /**
   * 程序启动
   *
   * @param event
   */
  void onAppStart(ApplicationReadyEvent event) throws Exception;

}
