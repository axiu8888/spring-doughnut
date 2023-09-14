package com.benefitj.spring.influxdb.pojo;

import com.benefitj.javastruct.JavaStructClass;
import com.benefitj.javastruct.JavaStructField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 睡眠生理数据
 */
@NoArgsConstructor
@SuperBuilder
@Data
@JavaStructClass
public class SleepPacket {

  /**
   * 时间戳 0~3
   */
  @JavaStructField(startAt = 0, size = 4)
  long time;

  /**
   * 心电：4 ~ 403
   */
  @JavaStructField(startAt = 4, size = 2, arrayLength = 200)
  int[] ecg;
  /**
   * 胸呼吸：404 ~ 453
   */
  @JavaStructField(startAt = 404, size = 2, arrayLength = 25)
  int[] chResp;
  /**
   * 腹呼吸：454 ~ 503
   */
  @JavaStructField(startAt = 454, size = 2, arrayLength = 25)
  int[] abdResp;
  /**
   * x轴：504 ~ 553
   */
  @JavaStructField(startAt = 504, size = 2, arrayLength = 25)
  int[] x;
  /**
   * y轴：554 ~ 603
   */
  @JavaStructField(startAt = 554, size = 2, arrayLength = 25)
  int[] y;
  /**
   * z轴：603 ~ 653
   */
  @JavaStructField(startAt = 603, size = 2, arrayLength = 25)
  int[] z;
  /**
   * 心率
   */
  @JavaStructField(startAt = 654, size = 2)
  int hr;
  /**
   * 呼吸率
   */
  @JavaStructField(startAt = 656, size = 1)
  int rr;
  /**
   * 血氧
   */
  @JavaStructField(startAt = 657, size = 1)
  int spo2;
  /**
   * 体位
   */
  @JavaStructField(startAt = 658, size = 1)
  int gesture;

}
