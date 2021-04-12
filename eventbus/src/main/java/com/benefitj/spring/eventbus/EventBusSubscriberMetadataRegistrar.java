package com.benefitj.spring.eventbus;

import com.benefitj.event.EventBusPoster;
import com.benefitj.spring.registrar.AnnotationMetadata;
import com.benefitj.spring.registrar.AnnotationMetadataRegistrar;
import com.benefitj.spring.registrar.MethodElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Method;

/**
 * EventBus监听注解注册
 *
 * @author DINGXIUAN
 */
public class EventBusSubscriberMetadataRegistrar implements AnnotationMetadataRegistrar {

  /**
   * 注册
   *
   * @param metadata    注解类的信息
   * @param beanFactory bean工厂
   */
  @Override
  public void register(AnnotationMetadata metadata, ConfigurableListableBeanFactory beanFactory) {
    if (metadata.getTargetClass().isAnnotationPresent(SubscriberIgnore.class)) {
      // ignore
      return;
    }

    Object bean = metadata.getBean();
    for (MethodElement element : metadata.getMethodElements()) {
      Method method = element.getMethod();
      if (!method.isAnnotationPresent(SubscriberIgnore.class)) {
        if (method.getParameterCount() != 1) {
          throw new IllegalArgumentException("EventBus仅支持单个参数的订阅！");
        }
        EventBusAdapterFactory factory;
        if (method.isAnnotationPresent(AdapterDefinition.class)) {
          AdapterDefinition definition = method.getAnnotation(AdapterDefinition.class);
          factory = beanFactory.getBean(definition.value());
        } else {
          factory = beanFactory.getBean(DefaultEventBusAdapterFactory.class);
        }
        EventBusAdapter adapter = factory.create(bean, method, method.getParameterTypes()[0]);
        register(beanFactory, adapter);
      }
    }
  }

  /**
   * 注册adapter
   *
   * @param beanFactory bean工厂
   * @param adapter     订阅的适配器
   */
  protected void register(ConfigurableListableBeanFactory beanFactory, EventBusAdapter adapter) {
    Method method = adapter.getMethod();
    if (method.isAnnotationPresent(PosterDefinition.class)) {
      PosterDefinition definition = method.getAnnotation(PosterDefinition.class);
      String[] names = definition.name();
      Class<? extends EventBusPoster>[] types = definition.type();
      if (StringUtils.isAllBlank(names)) {
        for (Class<? extends EventBusPoster> type : types) {
          EventBusPoster poster = beanFactory.getBean(type);
          poster.register(adapter);
        }
      } else {
        int minTypes = Math.min(names.length, types.length);
        for (int i = 0; i < names.length; i++) {
          EventBusPoster poster = beanFactory.getBean(names[i], types[i % minTypes]);
          poster.register(adapter);
        }
      }
    } else {
      EventBusPoster poster = beanFactory.getBean(EventBusPoster.class);
      poster.register(adapter);
    }
  }

}
