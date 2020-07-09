package com.benefitj.spring.eventbus;

import com.benefitj.event.EventAdapter;
import com.benefitj.event.EventBusPoster;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Map;

/**
 * 注册EventBus
 */
public class EventBusAdapterRegister<E extends EventAdapter> implements ApplicationListener {

  /**
   * adapter type
   */
  private Class<E> adapterType;
  /**
   * event poster
   */
  private EventBusPoster poster;

  public EventBusAdapterRegister() {
    this((Class<E>) EventAdapter.class, null);
  }

  public EventBusAdapterRegister(EventBusPoster poster) {
    this((Class<E>) EventAdapter.class, poster);
  }

  public EventBusAdapterRegister(Class<E> adapterType, EventBusPoster poster) {
    this.adapterType = adapterType;
    this.poster = poster;
  }

  @Override
  public final void onApplicationEvent(ApplicationEvent event) {
    if (event instanceof ApplicationReadyEvent) {
      register(((ApplicationReadyEvent) event).getApplicationContext());
    } else if (event instanceof ContextClosedEvent) {
      unregister(((ContextClosedEvent) event).getApplicationContext());
    }
  }

  /**
   * 注册
   *
   * @param context ApplicationContext
   */
  public void register(ApplicationContext context) {
    Map<String, E> adapters = context.getBeansOfType(getAdapterType());
    getPoster().register(adapters.values());
  }

  /**
   * 取消注册
   *
   * @param context ApplicationContext
   */
  public void unregister(ApplicationContext context) {
    Map<String, E> adapters = context.getBeansOfType(getAdapterType());
    getPoster().unregister(adapters.values());
  }

  public EventBusPoster getPoster() {
    return poster;
  }

  public void setPoster(EventBusPoster poster) {
    this.poster = poster;
  }

  public Class<E> getAdapterType() {
    return adapterType;
  }

  public void setAdapterType(Class<E> adapterType) {
    this.adapterType = adapterType;
  }
}
