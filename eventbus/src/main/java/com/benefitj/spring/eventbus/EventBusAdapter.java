package com.benefitj.spring.eventbus;

import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.spring.eventbus.event.NameEvent;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

public class EventBusAdapter {
  /**
   * 事件类型
   */
  private Class<?> eventType;
  /**
   * 名称
   */
  private Set<String> names = Collections.emptySet();
  /**
   * 规则
   */
  private Pattern pattern;
  /**
   * 是否强制根据名称匹配
   */
  private boolean forceName = false;

  private SimpleMethodInvoker methodInvoker;

  public EventBusAdapter(Object bean, Method method, Class<?> eventType) {
    this.eventType = eventType;
    this.methodInvoker = new SimpleMethodInvoker(bean, method);
  }

  public boolean support(Object o) {
    if (o instanceof NameEvent) {
      NameEvent event = ((NameEvent) o);
      return match(event.getMessage().getClass()) && match(event);
    }
    return match(o.getClass());
  }

  /**
   * 匹配类型
   *
   * @param type 事件类型
   * @return 返回是否匹配
   */
  protected boolean match(Class<?> type) {
    return getEventType().isAssignableFrom(type);
  }

  /**
   * 匹配名称和正则
   *
   * @param event 命名事件
   * @return 返回匹配规则
   */
  protected boolean match(NameEvent event) {
    if (StringUtils.isBlank(event.getName())) {
      return false;
    }
    if (getNames().contains(event.getName())) {
      return true;
    }
    try {
      return (getPattern() != null && getPattern().matcher(event.getName()).matches());
    } catch (Exception e) {
      throw new IllegalStateException("表达式错误: " + e.getMessage());
    }
  }

  /**
   * 接收事件
   *
   * @param event 事件
   */
  @Subscribe
  public void onEvent(Object event) {
    if (support(event)) {
      if (event instanceof NameEvent) {
        getMethodInvoker().invoke(((NameEvent) event).getMessage());
      } else {
        if (!isForceName()) {
          getMethodInvoker().invoke(event);
        }
      }
    }
  }

  public Class<?> getEventType() {
    return eventType;
  }

  public void setEventType(Class<?> eventType) {
    this.eventType = eventType;
  }

  public SimpleMethodInvoker getMethodInvoker() {
    return methodInvoker;
  }

  public void setMethodInvoker(SimpleMethodInvoker methodInvoker) {
    this.methodInvoker = methodInvoker;
  }

  public Set<String> getNames() {
    return names;
  }

  public void setNames(Set<String> names) {
    this.names = names;
  }

  public Pattern getPattern() {
    return pattern;
  }

  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  public boolean isForceName() {
    return forceName;
  }

  public void setForceName(boolean forceName) {
    this.forceName = forceName;
  }
}
