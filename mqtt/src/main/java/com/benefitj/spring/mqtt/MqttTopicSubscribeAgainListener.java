package com.benefitj.spring.mqtt;

public interface MqttTopicSubscribeAgainListener {
  /**
   * 客户端重连之后的重新订阅
   *
   * @param client 客户端
   */
  default void onSubscribeAgain(SingleMqttClient client) {
    // ~
  }

}
