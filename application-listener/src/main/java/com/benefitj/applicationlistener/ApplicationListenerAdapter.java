package com.benefitj.applicationlistener;

import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

public class ApplicationListenerAdapter implements ApplicationListener {

  public ApplicationListenerAdapter() {
  }

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    // 监听Spring Boot的生命周期
    if (event instanceof ApplicationEnvironmentPreparedEvent) {
      // ApplicationEnvironmentPreparedEvent： 初始化环境变量
      onApplicationEnvironmentPreparedEvent((ApplicationEnvironmentPreparedEvent) event);
    } else if (event instanceof ApplicationContextInitializedEvent) {
      // 上下文初始化
      onApplicationContextInitializedEvent((ApplicationContextInitializedEvent) event);
    } else if (event instanceof ApplicationStartingEvent) {
      // 应用启动中
      onApplicationStartingEvent((ApplicationStartingEvent) event);
    } else if (event instanceof ApplicationStartedEvent) {
      // 应用启动
      onApplicationStartedEvent((ApplicationStartedEvent) event);
    } else if (event instanceof ApplicationPreparedEvent) {
      // ApplicationPreparedEvent： 初始化完成
      onApplicationPreparedEvent((ApplicationPreparedEvent) event);
    } else if (event instanceof ContextRefreshedEvent) {
      // ContextRefreshedEvent： 应用刷新
      onContextRefreshedEvent((ContextRefreshedEvent) event);
    } else if (event instanceof ApplicationReadyEvent) {
      // ApplicationReadyEvent： 应用已启动完成
      onApplicationReadyEvent((ApplicationReadyEvent) event);
    } else if (event instanceof ContextStartedEvent) {
      // ContextStartedEvent： 应用启动
      onContextStartedEvent((ContextStartedEvent) event);
    } else if (event instanceof ContextStoppedEvent) {
      // ContextStoppedEvent： 应用停止
      onContextStoppedEvent((ContextStoppedEvent) event);
    } else if (event instanceof ContextClosedEvent) {
      // ContextClosedEvent： 应用关闭
      onContextClosedEventEvent((ContextClosedEvent) event);
    } else {
      // 其他事件
      onOtherApplicationEvent(event);
    }
  }

  /**
   * 初始化环境变量
   *
   * @param event 事件
   */
  public void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
    // ~
  }

  /**
   * 上下文初始化
   *
   * @param event 事件
   */
  public void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
    // ~
  }

  /**
   * 应用启动中
   *
   * @param event 事件
   */
  public void onApplicationStartingEvent(ApplicationStartingEvent event) {
    // ~
  }

  /**
   * 应用启动
   *
   * @param event 事件
   */
  public void onApplicationStartedEvent(ApplicationStartedEvent event) {
    // ~
  }

  /**
   * 初始化完成
   *
   * @param event 事件
   */
  public void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
    // ~
  }

  /**
   * 上下文刷新
   *
   * @param event 事件
   */
  public void onContextRefreshedEvent(ContextRefreshedEvent event) {
    // ~
  }

  /**
   * 应用启动
   *
   * @param event 事件
   */
  public void onContextStartedEvent(ContextStartedEvent event) {
    // ~
  }

  /**
   * 应用启动完毕
   *
   * @param event 事件
   */
  public void onApplicationReadyEvent(ApplicationReadyEvent event) {
    // ~
  }

  /**
   * 应用停止
   *
   * @param event 事件
   */
  public void onContextStoppedEvent(ContextStoppedEvent event) {
    // ~
  }

  /**
   * 应用关闭
   *
   * @param event 事件
   */
  public void onContextClosedEventEvent(ContextClosedEvent event) {
    // ~
  }

  /**
   * 其他事件
   *
   * @param event 事件
   */
  public void onOtherApplicationEvent(ApplicationEvent event) {
    // ~
  }
}
