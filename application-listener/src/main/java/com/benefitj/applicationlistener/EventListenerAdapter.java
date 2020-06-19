package com.benefitj.applicationlistener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.*;
import org.springframework.stereotype.Component;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * SpringBoot事件监听
 */
@ConditionalOnMissingBean(EventListenerAdapter.class)
@Component
public class EventListenerAdapter extends ApplicationListenerAdapter {

  @Autowired
  private ApplicationContext context;

  private final ThreadLocal<WeakReference<Map<String, IApplicationEventListener>>>
      listenerReference = ThreadLocal.withInitial(() -> new WeakReference<>(null));

  public EventListenerAdapter() {
    super();
  }

  @Override
  public final void onApplicationEvent(ApplicationEvent event) {
    super.onApplicationEvent(event);
  }

  public Map<String, IApplicationEventListener> getListeners() {
    Map<String, IApplicationEventListener> listeners = listenerReference.get().get();
    if (listeners == null) {
      listeners = context.getBeansOfType(IApplicationEventListener.class);
      listenerReference.set(new WeakReference<>(listeners));
    }
    return listeners;
  }

  @Override
  public void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
    getListeners().forEach((s, listener) -> listener.onApplicationEnvironmentPreparedEvent(event));
  }

  @Override
  public void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
    getListeners().forEach((s, listener) -> listener.onApplicationContextInitializedEvent(event));
  }

  @Override
  public void onApplicationStartingEvent(ApplicationStartingEvent event) {
    getListeners().forEach((s, listener) -> listener.onApplicationStartingEvent(event));
  }

  @Override
  public void onApplicationStartedEvent(ApplicationStartedEvent event) {
    getListeners().forEach((s, listener) -> listener.onApplicationStartedEvent(event));
  }

  @Override
  public void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
    getListeners().forEach((s, listener) -> listener.onApplicationPreparedEvent(event));
  }

  @Override
  public void onContextRefreshedEvent(ContextRefreshedEvent event) {
    getListeners().forEach((s, listener) -> listener.onContextRefreshedEvent(event));
  }

  @Override
  public void onApplicationReadyEvent(ApplicationReadyEvent event) {
    getListeners().forEach((s, listener) -> listener.onApplicationReadyEvent(event));
  }

  @Override
  public void onContextStartedEvent(ContextStartedEvent event) {
    getListeners().forEach((s, listener) -> listener.onContextStartedEvent(event));
  }

  @Override
  public void onContextStoppedEvent(ContextStoppedEvent event) {
    getListeners().forEach((s, listener) -> listener.onContextStoppedEvent(event));
  }

  @Override
  public void onContextClosedEventEvent(ContextClosedEvent event) {
    getListeners().forEach((s, listener) -> listener.onContextClosedEventEvent(event));
  }

  @Override
  public void onOtherApplicationEvent(ApplicationEvent event) {
    getListeners().forEach((s, listener) -> listener.onOtherApplicationEvent(event));
  }

}
