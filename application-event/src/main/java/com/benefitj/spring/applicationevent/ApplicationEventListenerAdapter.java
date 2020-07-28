package com.benefitj.spring.applicationevent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * SpringBoot事件监听
 */
@ConditionalOnMissingBean(ApplicationEventListenerAdapter.class)
@Component
public class ApplicationEventListenerAdapter extends ApplicationListenerAdapter {

  @Autowired
  private ApplicationContext context;

  private final Map<Class<?>, Map<String, ?>> listenerMap = new WeakHashMap<>();
  private final Function<Class<?>, Map<String, ?>> func = type -> context.getBeansOfType(type);

  public ApplicationEventListenerAdapter() {
    super();
  }

  @Override
  public final void onApplicationEvent(ApplicationEvent event) {
    super.onApplicationEvent(event);
  }

  /**
   * 获取监听
   */
  public <T> Map<String, T> getListeners(Class<T> listenerType) {
    return (Map<String, T>) listenerMap.computeIfAbsent(listenerType, func);
  }

  @Override
  public void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
    apply(IApplicationEnvironmentPreparedEventListener.class, (name, listener) -> listener.onApplicationEnvironmentPreparedEvent(event));
  }

  @Override
  public void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
    apply(IApplicationContextInitializedEventListener.class, (name, listener) -> listener.onApplicationContextInitializedEvent(event));
  }

  @Override
  public void onApplicationStartingEvent(ApplicationStartingEvent event) {
    apply(IApplicationStartingEventListener.class, (name, listener) -> listener.onApplicationStartingEvent(event));
  }

  @Override
  public void onApplicationStartedEvent(ApplicationStartedEvent event) {
    apply(IApplicationStartedEventListener.class, (name, listener) -> listener.onApplicationStartedEvent(event));
  }

  @Override
  public void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
    apply(IApplicationPreparedEventListener.class, (name, listener) -> listener.onApplicationPreparedEvent(event));
  }

  @Override
  public void onContextRefreshedEvent(ContextRefreshedEvent event) {
    apply(IContextRefreshedEventListener.class, (name, listener) -> listener.onContextRefreshedEvent(event));
  }

  @Override
  public void onApplicationReadyEvent(ApplicationReadyEvent event) {
    apply(IApplicationReadyEventListener.class, (name, listener) -> listener.onApplicationReadyEvent(event));
  }

  @Override
  public void onContextStartedEvent(ContextStartedEvent event) {
    apply(IContextStartedEventListener.class, (name, listener) -> listener.onContextStartedEvent(event));
  }

  @Override
  public void onContextStoppedEvent(ContextStoppedEvent event) {
    apply(IContextStoppedEventListener.class, (name, listener) -> listener.onContextStoppedEvent(event));
  }

  @Override
  public void onContextClosedEvent(ContextClosedEvent event) {
    apply(IContextClosedEventListener.class, (name, listener) -> listener.onContextClosedEvent(event));
  }

  @Override
  public void onServletRequestHandledEvent(ServletRequestHandledEvent event) {
    apply(IServletRequestHandledEventListener.class, (name, listener) -> listener.onServletRequestHandledEvent(event));
  }

  @Override
  public void onOtherApplicationEvent(ApplicationEvent event) {
    apply(IOtherApplicationEventListener.class, (name, listener) -> listener.onOtherApplicationEvent(event));
  }

  public <T> void apply(Class<T> type, BiConsumer<String, T> consumer) {
    getListeners(type).forEach(consumer);
  }

}
