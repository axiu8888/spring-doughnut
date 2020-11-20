package com.benefitj.spring.influxdb.dto;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "retentionPolicy")
public class InfluxRetentionPolicy {

  @Column(name = "name")
  private String name;
  @Column(name = "duration")
  private String duration;
  @Column(name = "shardGroupDuration")
  private String shardGroupDuration;
  @Column(name = "replicaN")
  private long replicaN;
  @Column(name = "default")
  private boolean defaultFlag;

  public InfluxRetentionPolicy() {
  }

  public InfluxRetentionPolicy(String name, String duration, String shardGroupDuration, long replicaN, boolean defaultFlag) {
    this.name = name;
    this.duration = duration;
    this.shardGroupDuration = shardGroupDuration;
    this.replicaN = replicaN;
    this.defaultFlag = defaultFlag;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getShardGroupDuration() {
    return shardGroupDuration;
  }

  public void setShardGroupDuration(String shardGroupDuration) {
    this.shardGroupDuration = shardGroupDuration;
  }

  public long getReplicaN() {
    return replicaN;
  }

  public void setReplicaN(long replicaN) {
    this.replicaN = replicaN;
  }

  public boolean isDefaultFlag() {
    return defaultFlag;
  }

  public void setDefaultFlag(boolean defaultFlag) {
    this.defaultFlag = defaultFlag;
  }
}
