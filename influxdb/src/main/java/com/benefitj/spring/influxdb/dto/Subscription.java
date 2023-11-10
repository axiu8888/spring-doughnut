package com.benefitj.spring.influxdb.dto;

import java.util.List;

/**
 * 订阅配置
 */
public class Subscription {

  private String db;
  private String retention_policy;
  private String name;
  private String mode;
  private List<String> destinations;

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public String getRetention_policy() {
    return retention_policy;
  }

  public void setRetention_policy(String retention_policy) {
    this.retention_policy = retention_policy;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public List<String> getDestinations() {
    return destinations;
  }

  public void setDestinations(List<String> destinations) {
    this.destinations = destinations;
  }

}
