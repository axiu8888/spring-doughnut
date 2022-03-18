package com.benefitj.spring.mqtt.event;

import com.benefitj.spring.JsonUtils;

/**
 * 将事件转换为JSON数据
 */
public class JsonEventConverter implements EventConverter {

  @Override
  public byte[] convert(EventDescriptor descriptor, Object event) {
    return JsonUtils.toJsonBytes(event);
  }
}
