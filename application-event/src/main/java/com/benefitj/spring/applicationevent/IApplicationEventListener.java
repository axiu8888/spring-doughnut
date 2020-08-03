package com.benefitj.spring.applicationevent;

import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

public interface IApplicationEventListener extends
    IApplicationEnvironmentPreparedEventListener,
    IApplicationContextInitializedEventListener,
    IApplicationStartingEventListener,
    IApplicationStartedEventListener,
    IApplicationPreparedEventListener,
    IContextRefreshedEventListener,
    IContextStartedEventListener,
    IApplicationReadyEventListener,
    IContextStoppedEventListener,
    IContextClosedEventListener,
    IOtherApplicationEventListener {

  /**
   * 初始化环境变量
   *
   * @param event 事件
   */
  @Override
  default void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
    // ~
  }

  /**
   * 上下文初始化
   *
   * @param event 事件
   */
  @Override
  default void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
    // ~
  }

  /**
   * 应用启动中
   *
   * @param event 事件
   */
  @Override
  default void onApplicationStartingEvent(ApplicationStartingEvent event) {
    // ~
  }

  /**
   * 应用启动
   *
   * @param event 事件
   */
  @Override
  default void onApplicationStartedEvent(ApplicationStartedEvent event) {
    // ~
  }

  /**
   * 初始化完成
   *
   * @param event 事件
   */
  @Override
  default void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
    // ~
  }

  /**
   * 上下文刷新
   *
   * @param event 事件
   */
  @Override
  default void onContextRefreshedEvent(ContextRefreshedEvent event) {
    // ~
  }

  /**
   * 应用启动
   *
   * @param event 事件
   */
  @Override
  default void onContextStartedEvent(ContextStartedEvent event) {
    // ~
  }

  /**
   * 应用启动完毕
   *
   * @param event 事件
   */
  @Override
  default void onApplicationReadyEvent(ApplicationReadyEvent event) {
    // ~
  }

  /**
   * 应用停止
   *
   * @param event 事件
   */
  @Override
  default void onContextStoppedEvent(ContextStoppedEvent event) {
    // ~
  }

  /**
   * 应用关闭
   *
   * @param event 事件
   */
  @Override
  default void onContextClosedEvent(ContextClosedEvent event) {
    // ~
  }

  /**
   * 其他事件
   *
   * @param event 事件
   */
  @Override
  default void onOtherApplicationEvent(ApplicationEvent event) {
    // ~
  }

}
