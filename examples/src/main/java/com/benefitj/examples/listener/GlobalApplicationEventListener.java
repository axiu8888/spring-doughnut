package com.benefitj.examples.listener;

import com.benefitj.spring.applicationevent.ApplicationEventAdapter;
import com.benefitj.spring.applicationevent.ApplicationEventListener;
import com.benefitj.spring.applicationevent.EventAdapterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import java.lang.reflect.Method;

@Component
public class GlobalApplicationEventListener {

  private static final Logger log = LoggerFactory.getLogger(GlobalApplicationEventListener.class);

  @ApplicationEventListener
  public void onApplicationEvent(ApplicationEvent event) {
    if (event instanceof ServletRequestHandledEvent) {
      ServletRequestHandledEvent srhe = (ServletRequestHandledEvent) event;
      log.info("onServletRequestHandledEvent: {}, clientAddress: {}", srhe.getRequestUrl(), srhe.getClientAddress());
    } else {
      log.info("onEvent: {}", event.getClass());
    }
  }

  @ApplicationEventListener(adapterFactory = CustomApplicationEventAdapterFactory.class)
  public void onApplicationEvent2(ApplicationEvent event) {
    log.info("_____onEvent2: {}", event.getClass());
  }

  @Component
  public static class CustomApplicationEventAdapterFactory implements EventAdapterFactory {

    @Override
    public ApplicationEventAdapter create(Object bean,
                                          Method method,
                                          Class<? extends ApplicationEvent> eventType) {
      CustomApplicationEventAdapter adapter = new CustomApplicationEventAdapter();
      adapter.setBean(bean);
      adapter.setMethod(method);
      adapter.setEventType(eventType);
      return adapter;
    }
  }

  public static class CustomApplicationEventAdapter extends ApplicationEventAdapter {

    public CustomApplicationEventAdapter() {
    }

    @Override
    public boolean support(ApplicationEvent event) {
      return ContextClosedEvent.class.isAssignableFrom(event.getClass());
    }
  }
}
