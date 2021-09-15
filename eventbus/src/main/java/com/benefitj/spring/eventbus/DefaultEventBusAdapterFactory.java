package com.benefitj.spring.eventbus;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 默认的adapter工厂实现
 */
public class DefaultEventBusAdapterFactory implements EventBusAdapterFactory {

  @Override
  public EventBusAdapter create(Object bean, Method method, Class<?> eventType, SubscriberName name) {
    EventBusAdapter adapter = new EventBusAdapter();
    adapter.setBean(bean);
    adapter.setMethod(method);
    adapter.setEventType(eventType);
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

}
