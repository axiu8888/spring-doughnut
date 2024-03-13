package com.hsrg.collectorrelay.parse.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 血压数据包
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class MmhgPacket {

  /**
   * 设备ID
   */
  String deviceId;
  /**
   * 时间
   */
  long time;
  /**
   * 错误码
   */
  Integer err;
  /**
   * 收缩压
   */
  Integer systolic;
  /**
   * 舒张压
   */
  Integer diastolic;
  /**
   * 平均压
   */
  Integer avg;
  /**
   * 心率值
   */
  Integer bloodHr;
  /**
   * 体位
   */
  Integer position;
  /**
   * 测量方式
   */
  Integer measure;
  /**
   * 血压计类型,0是电子血压  1是动态血压 2-蓝牙血压
   */
  Integer type;

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public Integer getErr() {
    return err;
  }

  public void setErr(Integer err) {
    this.err = err;
  }

  public Integer getSystolic() {
    return systolic;
  }

  public void setSystolic(Integer systolic) {
    this.systolic = systolic;
  }

  public Integer getDiastolic() {
    return diastolic;
  }

  public void setDiastolic(Integer diastolic) {
    this.diastolic = diastolic;
  }

  public Integer getAvg() {
    return avg;
  }

  public void setAvg(Integer avg) {
    this.avg = avg;
  }

  public Integer getBloodHr() {
    return bloodHr;
  }

  public void setBloodHr(Integer bloodHr) {
    this.bloodHr = bloodHr;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public Integer getMeasure() {
    return measure;
  }

  public void setMeasure(Integer measure) {
    this.measure = measure;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
}
