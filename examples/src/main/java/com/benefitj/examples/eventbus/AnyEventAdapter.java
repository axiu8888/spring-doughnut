package com.benefitj.examples.eventbus;

import com.alibaba.fastjson.JSON;
import com.benefitj.event.BaseEventAdapter;
import com.benefitj.event.Event;
import com.benefitj.event.EventBusPoster;
import com.benefitj.spring.eventbus.PosterDefinition;
import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;

@Component
public class AnyEventAdapter extends BaseEventAdapter<Event> {

  @Override
  public void process(Event event) {
    logger.info("接收到事件1: {}", JSON.toJSONString(event));
  }

  @PosterDefinition(type = EventBusPoster.class)
  @Subscribe
  public void onEvent2(Event event) {
    logger.info("接收到事件2: {}", JSON.toJSONString(event));
  }

}
