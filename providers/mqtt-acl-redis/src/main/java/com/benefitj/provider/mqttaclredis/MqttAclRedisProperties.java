package com.benefitj.provider.mqttaclredis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mqtt-acl-redis")
public class MqttAclRedisProperties {


  /**
   * Database index used by the connection factory.
   */
  private int database = 0;

  /**
   * Redis server host.
   */
  private String host = "localhost";

  /**
   * Login password of the redis server.
   */
  private String password;

  /**
   * Redis server port.
   */
  private int port = 6379;
  /**
   * 初始化文件的路径
   */
  private String initFile;

  public int getDatabase() {
    return database;
  }

  public void setDatabase(int database) {
    this.database = database;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getInitFile() {
    return initFile;
  }

  public void setInitFile(String initFile) {
    this.initFile = initFile;
  }
}
