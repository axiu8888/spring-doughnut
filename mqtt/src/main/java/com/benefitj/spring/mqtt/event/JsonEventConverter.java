package com.benefitj.spring.mqtt.event;

import com.alibaba.fastjson2.JSON;

/**
 * 将事件转换为JSON数据
 */
public class JsonEventConverter implements EventConverter {

  @Override
  public byte[] convert(EventDescriptor descriptor, Object event) {
    return JSON.toJSONBytes(event);
  }
}
