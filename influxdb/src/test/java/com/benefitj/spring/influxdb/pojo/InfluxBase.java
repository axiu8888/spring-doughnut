package com.benefitj.spring.influxdb.pojo;


import com.benefitj.spring.influxdb.annotation.Column;
import com.benefitj.spring.influxdb.annotation.TimeColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class InfluxBase {
  /**
   * 患者ID
   */
  @Column(name = "person_zid", tag = true)
  private String personZid;
  /**
   * 住院患者ID
   */
  @Column(name = "patient_id", tag = true)
  private String patientId;
  /**
   * 时间戳
   */
  @TimeColumn
  @Column(name = "time")
  private Long time;
  /**
   * 包序号
   */
  @Column(name = "package_sn")
  private Integer packageSn;
  /**
   * 是否为模拟数据
   */
  @Column(name = "simulate")
  private Boolean simulate;
  /**
   * 数据类型：六分钟步行、呼吸训练、监护...
   */
  @Column(name = "data_type", tag = true)
  private String dataType;
}
