package com.benefitj.spring.mqtt;

public class MqttPahoClientException extends RuntimeException {

  public MqttPahoClientException() {
  }

  public MqttPahoClientException(String message) {
    super(message);
  }

  public MqttPahoClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public MqttPahoClientException(Throwable cause) {
    super(cause);
  }

  public MqttPahoClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
