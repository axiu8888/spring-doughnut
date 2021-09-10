package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;

public interface AppStartListener {

  /**
   * 程序启动
   *
   * @param event
   */
  void onAppStart(ApplicationReadyEvent event);

}
