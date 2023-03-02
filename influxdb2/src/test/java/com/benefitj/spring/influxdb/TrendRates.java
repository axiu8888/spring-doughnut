package com.benefitj.spring.influxdb;

import com.benefitj.spring.influxdb.annotation.Column;
import com.benefitj.spring.influxdb.annotation.Measurement;
import com.benefitj.spring.influxdb.annotation.TimeColumn;

import java.util.concurrent.TimeUnit;

/**
 */
@Measurement(name = "sys_trend_rates", timeUnit = TimeUnit.SECONDS)
public class TrendRates {
  /**
   * 时间戳
   */
  @TimeColumn
  @Column(name = "time")
  private Long time;
  /**
   * 设备ID
   *
   * 通过重新定义字段，覆盖父类的字段，让 device_id 作为tag
   */
  @Column(name = "deviceId", tag = true)
  private String deviceId;
  /**
   * 心率
   */
  @Column(name = "heartRate")
  private Short heartRate;
  /**
   * 呼吸率
   */
  @Column(name = "respRate")
  private Short respRate;
  /**
   * 血氧
   */
  @Column(name = "spo2")
  private Byte spo2;
  /**
   * 体位
   */
  @Column(name = "gesture")
  private Integer gesture;
  /**
   * 能耗, 卡路里
   */
  @Column(name = "energy")
  private Double energy;
  /**
   * 步数
   */
  @Column(name = "step")
  private Short step;
  /**
   * 默认秒；允许值：ss mm
   */
  @Column(name = "type")
  private String type;

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public Short getHeartRate() {
    return heartRate;
  }

  public void setHeartRate(Short heartRate) {
    this.heartRate = heartRate;
  }

  public Short getRespRate() {
    return respRate;
  }

  public void setRespRate(Short respRate) {
    this.respRate = respRate;
  }

  public Byte getSpo2() {
    return spo2;
  }

  public void setSpo2(Byte spo2) {
    this.spo2 = spo2;
  }

  public Integer getGesture() {
    return gesture;
  }

  public void setGesture(Integer gesture) {
    this.gesture = gesture;
  }

  public Double getEnergy() {
    return energy;
  }

  public void setEnergy(Double energy) {
    this.energy = energy;
  }

  public Short getStep() {
    return step;
  }

  public void setStep(Short step) {
    this.step = step;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
