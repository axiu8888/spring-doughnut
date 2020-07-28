package com.benefitj.examples.listener;

import com.benefitj.spring.applicationevent.IApplicationEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

@Component
public class GlobalApplicationEventListener implements IApplicationEventListener {

  private static final Logger log = LoggerFactory.getLogger(GlobalApplicationEventListener.class);

  @Override
  public void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
    log.info("onApplicationEnvironmentPreparedEvent");
  }

  @Override
  public void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
    log.info("onApplicationContextInitializedEvent");
  }

  @Override
  public void onApplicationStartingEvent(ApplicationStartingEvent event) {
    log.info("onApplicationStartingEvent");
  }

  @Override
  public void onApplicationStartedEvent(ApplicationStartedEvent event) {
    log.info("onApplicationStartedEvent");
  }

  @Override
  public void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
    log.info("onApplicationPreparedEvent");
  }

  @Override
  public void onContextRefreshedEvent(ContextRefreshedEvent event) {
    log.info("onContextRefreshedEvent");
  }

  @Override
  public void onContextStartedEvent(ContextStartedEvent event) {
    log.info("onContextStartedEvent");
  }

  @Override
  public void onApplicationReadyEvent(ApplicationReadyEvent event) {
    log.info("onApplicationReadyEvent");
  }

  @Override
  public void onContextStoppedEvent(ContextStoppedEvent event) {
    log.info("onContextStoppedEvent");
  }

  @Override
  public void onContextClosedEvent(ContextClosedEvent event) {
    log.info("onContextClosedEventEvent");
  }

  @Override
  public void onServletRequestHandledEvent(ServletRequestHandledEvent event) {
    log.info("onServletRequestHandledEvent: " + event.getRequestUrl());
  }

  @Override
  public void onOtherApplicationEvent(ApplicationEvent event) {
    log.info("onOtherApplicationEvent: " + event.getClass());
  }
}
