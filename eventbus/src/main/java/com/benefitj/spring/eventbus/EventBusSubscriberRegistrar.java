package com.benefitj.spring.eventbus;

import com.benefitj.event.EventBusPoster;
import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.MetadataHandler;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * EventBus监听注解注册
 *
 * @author DINGXIUAN
 */
public class EventBusSubscriberRegistrar extends AnnotationBeanProcessor implements MetadataHandler {

  private EventBusPoster poster;

  public EventBusSubscriberRegistrar() {
    this.register(Subscribe.class);
    this.setMetadataHandler(this);
  }

  public EventBusSubscriberRegistrar(EventBusPoster poster) {
    this();
    this.poster = poster;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean.getClass().isAssignableFrom(SubscriberIgnore.class)) {
      return bean;
    }
    return super.postProcessAfterInitialization(bean, beanName);
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      if (metadata.getMethod().isAnnotationPresent(SubscriberIgnore.class)) {
        // ignore
        continue;
      }

      Method method = metadata.getMethod();
      if (!method.isAnnotationPresent(SubscriberIgnore.class)) {
        if (method.getParameterCount() != 1) {
          throw new IllegalArgumentException("EventBus仅支持单个参数的订阅！");
        }
        SubscriberName name = method.isAnnotationPresent(SubscriberName.class)
            ? method.getAnnotation(SubscriberName.class) : null;
        EventBusAdapter adapter = createAdapter(metadata.getBean(), method, method.getParameterTypes()[0], name);
        getPoster().register(adapter);
      }
    }
  }

  public EventBusAdapter createAdapter(Object bean, Method method, Class<?> eventType, SubscriberName name) {
    EventBusAdapter adapter = new EventBusAdapter(bean, method, eventType);
    if (name != null) {
      // 名称
      adapter.setNames(Arrays.stream(name.name())
          .filter(StringUtils::isNotBlank)
          .map(String::trim)
          .collect(Collectors.toSet()));
      // 正则表达式
      adapter.setPattern(Pattern.compile(name.pattern()));
      // 是否强制匹配规则
      adapter.setForceName(name.force());

      if (adapter.getNames().isEmpty() && adapter.getPattern() == null) {
        throw new IllegalStateException("[" + bean.getClass().getName() + "." + method.getName() + "], name和pattern需要有一个不为空!");
      }
    }
    return adapter;
  }

  public EventBusPoster getPoster() {
    return poster;
  }

  public void setPoster(EventBusPoster poster) {
    this.poster = poster;
  }
}
