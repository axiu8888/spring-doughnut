package com.benefitj.examples.eventbus;

import com.alibaba.fastjson.JSON;
import com.benefitj.event.BaseEventAdapter;
import com.benefitj.event.Event;
import org.springframework.stereotype.Component;

@Component
public class AnyEventAdapter extends BaseEventAdapter<Event> {

  @Override
  public void process(Event event) {
    logger.info("接收到事件: " + JSON.toJSONString(event));
  }

}
