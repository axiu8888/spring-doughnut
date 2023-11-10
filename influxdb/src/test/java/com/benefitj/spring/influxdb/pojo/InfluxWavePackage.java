package com.benefitj.spring.influxdb.pojo;

import com.benefitj.spring.influxdb.annotation.Column;
import com.benefitj.spring.influxdb.annotation.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 波形包
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Measurement(name = "hs_wave_package", timeUnit = TimeUnit.SECONDS)
public class InfluxWavePackage extends InfluxBase {

  /**
   * 心电
   */
  @Column(name = "ecg_points")
  private String ecgPoints;
  /**
   * 胸呼吸
   */
  @Column(name = "resp_points")
  private String respPoints;
  /**
   * 腹呼吸
   */
  @Column(name = "abdominal_resp_points")
  private String abdominalRespPoints;
  /**
   * 三轴加速度: x轴
   */
  @Column(name = "x_points")
  private String xPoints;
  /**
   * 三轴加速度: y轴
   */
  @Column(name = "y_points")
  private String yPoints;
  /**
   * 三轴加速度: z轴
   */
  @Column(name = "z_points")
  private String zPoints;


  /**
   * 加速度拟合值（0-1024）
   */
  @Column(name = "xyz_out_point")
  private String xyzOutPoint;
  /**
   * 三轴加速度SVM，使用 {@link InfluxWavePackage#xyzOutPoint}
   */
  @Deprecated
  @Column(name = "svm_points")
  private String svmPoints;
  /**
   * 数据值
   */
  @Column(name = "spo2_points")
  private String spo2Points;

  /**
   * 心电导联脱落状态
   */
  @Column(name = "ecg_conn_state")
  private Byte ecgConnState;
  /**
   * 胸呼吸连接状态
   */
  @Column(name = "resp_conn_state")
  private Byte respConnState;
  /**
   * 腹呼吸连接状态
   */
  @Column(name = "abdomina_conn_state")
  private Byte abdominaConnState;

  /**
   * 潮气量
   */
  @Column(name = "tidal_volume")
  private String tidalVolume;
  /**
   * 潮气量
   */
  @Column(name = "volume")
  private Short volume;
  /**
   * 实时胸腹呼吸共享比
   */
  @Column(name = "ca_ratio")
  private Integer caRatio;
  /**
   * 实时呼吸比
   */
  @Column(name = "ei_ratio")
  private Integer eiRatio;

}
