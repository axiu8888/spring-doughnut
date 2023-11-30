package com.benefitj.examples.eventbus;

import com.alibaba.fastjson2.JSON;
import com.benefitj.event.BaseEventAdapter;
import com.benefitj.event.Event;
import com.benefitj.examples.vo.IdEvent;
import com.benefitj.spring.eventbus.SubscriberIgnore;
import com.benefitj.spring.eventbus.SubscriberName;
import com.google.common.eventbus.Subscribe;
import org.springframework.stereotype.Component;

//@SubscriberIgnore  // 忽略全部
@Component
public class AnyEventAdapter extends BaseEventAdapter<Event> {

  @Override
  public void process(Event event) {
    log.info("接收到事件1: {}, {}", JSON.toJSONString(event), event.getClass());
  }

  @Subscribe
  public void onEvent2(Event event) {
    log.info("接收到事件2: {}, {}", JSON.toJSONString(event), event.getClass());
  }

  @SubscriberIgnore
  @Subscribe
  public void onEventIgnore(Event event) {
    // 不可达
    log.info("接收到事件3: {}, {}", JSON.toJSONString(event), event.getClass());
  }

  /**
   * 匹配名称
   */
  @SubscriberName(name = "id")
  @Subscribe
  public void onEvent4(IdEvent event) {
    log.info("接收到事件4: {}, {}", JSON.toJSONString(event), event.getClass());
  }

  /**
   * 匹配正则，匹配全部
   */
  @SubscriberName(pattern = "([\\s\\S]*)")
  @Subscribe
  public void onEvent5(IdEvent event) {
    log.info("接收到事件5: {}, {}", JSON.toJSONString(event), event.getClass());
  }

}
