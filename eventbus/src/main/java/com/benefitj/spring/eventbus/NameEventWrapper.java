package com.benefitj.spring.eventbus;

import com.benefitj.event.EventWrapper;
import com.benefitj.spring.eventbus.event.BasicNameEvent;
import org.apache.commons.lang3.StringUtils;

/**
 * 具有名称的事件包装器
 */
public class NameEventWrapper implements EventWrapper {

  @Override
  public Object wrap(Object o) {
    Class<?> type = o.getClass();
    if (type.isAnnotationPresent(EventName.class)) {
      String name = type.getAnnotation(EventName.class).value();
      if (StringUtils.isNotBlank(name)) {
        return BasicNameEvent.of(name, o);
      }
    }
    return o;
  }

}
