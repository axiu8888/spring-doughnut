package com.benefitj.spring.eventbus.event;

public interface NameEvent {

  /**
   * 设置名称
   *
   * @param name 名称
   */
  void setName(String name);

  /**
   * 名称
   */
  String getName();

  /**
   * 设置消息
   *
   * @param message 消息
   */
  void setMessage(Object message);

  /**
   * 获取消息
   */
  Object getMessage();

  static NameEvent of(String name, Object message) {
    return BasicNameEvent.of(name, message);
  }

}
