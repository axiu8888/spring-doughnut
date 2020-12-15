package com.benefitj.examples.listener;

import com.benefitj.spring.applicationevent.ApplicationEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

@Component
public class GlobalApplicationEventListener {

  private static final Logger log = LoggerFactory.getLogger(GlobalApplicationEventListener.class);

  @ApplicationEventListener
  public void onEvent(ApplicationEvent event) {
    if (event instanceof ServletRequestHandledEvent) {
      ServletRequestHandledEvent srhe = (ServletRequestHandledEvent) event;
      log.info("onServletRequestHandledEvent: {}, clientAddress: {}", srhe.getRequestUrl(), srhe.getClientAddress());
    } else {
      log.info("onEvent: {}", event.getClass());
    }
  }

}
