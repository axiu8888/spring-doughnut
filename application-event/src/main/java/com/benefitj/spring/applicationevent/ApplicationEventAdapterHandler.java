package com.benefitj.spring.applicationevent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationEventAdapterHandler implements ApplicationListener<ApplicationEvent> {

  private final Map<String, ApplicationEventAdapter> adapters = new ConcurrentHashMap<>();

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  protected ApplicationEventAdapterHandler() {
  }

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    getAdapters().forEach((name, adapter) -> {
      try {
        adapter.onApplicationEvent(event);
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    });
  }

  public Map<String, ApplicationEventAdapter> getAdapters() {
    return adapters;
  }

  /**
   * 注册
   *
   * @param name
   * @param adapter
   * @return
   */
  public boolean register(String name, ApplicationEventAdapter adapter) {
    return getAdapters().putIfAbsent(name, adapter) == null;
  }

  /**
   * 取消注册
   *
   * @param name 名称
   * @return 返回被取消的对象
   */
  public ApplicationEventAdapter unregister(String name) {
    return getAdapters().remove(name);
  }


}
