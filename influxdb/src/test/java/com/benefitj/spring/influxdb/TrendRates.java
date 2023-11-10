package com.benefitj.spring.influxdb;

import com.benefitj.spring.influxdb.annotation.Column;
import com.benefitj.spring.influxdb.annotation.Measurement;
import com.benefitj.spring.influxdb.annotation.TimeColumn;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@SuperBuilder
@NoArgsConstructor
@Data
@Measurement(name = "sys_trend_rates", timeUnit = TimeUnit.SECONDS)
public class TrendRates {
  /**
   * 时间戳
   */
  @TimeColumn
  @Column(name = "time")
  Long time;
  /**
   * 设备ID
   * <p>
   * 通过重新定义字段，覆盖父类的字段，让 device_id 作为tag
   */
  @Column(name = "deviceId", tag = true)
  String deviceId;
  /**
   * 心率
   */
  @Column(name = "heartRate")
  Short heartRate;
  /**
   * 呼吸率
   */
  @Column(name = "respRate")
  Short respRate;
  /**
   * 血氧
   */
  @Column(name = "spo2")
  Byte spo2;
  /**
   * 体位
   */
  @Column(name = "gesture")
  Integer gesture;
  /**
   * 能耗, 卡路里
   */
  @Column(name = "energy")
  Double energy;
  /**
   * 步数
   */
  @Column(name = "step")
  Short step;
  /**
   * 默认秒；允许值：ss mm
   */
  @Column(name = "type")
  String type;
  /**
   * 描述
   */
  @Column(name = "description")
  String description;

}
