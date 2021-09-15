package com.benefitj.spring.eventbus.event;

import org.apache.commons.lang3.StringUtils;

public class BasicNameEvent implements NameEvent {

  public static NameEvent of(String name, Object message) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("name必须不能为空!");
    }
    return new BasicNameEvent(name, message);
  }

  private String name;
  private Object message;

  public BasicNameEvent() {
  }

  public BasicNameEvent(String name, Object message) {
    this.name = name;
    this.message = message;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Object getMessage() {
    return message;
  }

  @Override
  public void setMessage(Object message) {
    this.message = message;
  }
}