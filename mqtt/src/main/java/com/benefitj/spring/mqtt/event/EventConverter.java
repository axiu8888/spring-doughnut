package com.benefitj.spring.mqtt.event;

/**
 * 事件转换器
 */
public interface EventConverter {

  /**
   * 转换
   *
   * @param descriptor 描述符
   * @param event      事件
   * @return 返回转换后的字节数据
   */
  byte[] convert(EventDescriptor descriptor, Object event);

  /**
   * 转换器类型
   */
  default Class<? extends EventConverter> getConverterType() {
    return getClass();
  }

}
