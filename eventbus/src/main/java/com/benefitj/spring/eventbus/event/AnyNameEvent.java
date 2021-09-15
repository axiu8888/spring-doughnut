package com.benefitj.spring.eventbus.event;

import com.benefitj.event.Event;

public class AnyNameEvent extends BasicNameEvent implements Event {

  public static AnyNameEvent of(String name, Object event) {
    return new AnyNameEvent(name, event);
  }

  public AnyNameEvent() {
  }

  public AnyNameEvent(String name, Object message) {
    super(name, message);
  }
}
