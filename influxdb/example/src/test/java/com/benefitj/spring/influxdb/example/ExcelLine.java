package com.benefitj.spring.influxdb.example;

import com.alibaba.excel.annotation.ExcelProperty;

public class ExcelLine {

  /**
   * 时间
   */
  @ExcelProperty("时间")
  private String time;
  /**
   * 心电：每5毫秒一个值，200
   */
  @ExcelProperty("心电")
  private String ecgWave;
  /**
   * 血氧：每20毫秒一个值，50
   */
  @ExcelProperty("脉搏波")
  private String pulseWave;
  /**
   * 胸呼吸：每40毫秒一个值，25
   */
  @ExcelProperty("胸呼吸")
  private String respWave;
  /**
   * 腹呼吸：每40毫秒一个值，25
   */
  @ExcelProperty("腹呼吸")
  private String abdominalRespWave;
  /**
   * 三轴加速度: x轴，每40毫秒一个值，25
   */
  @ExcelProperty("x轴")
  private String xWave;
  /**
   * 三轴加速度: y轴，每40毫秒一个值，25
   */
  @ExcelProperty("y轴")
  private String yWave;
  /**
   * 三轴加速度: z轴，每40毫秒一个值，25
   */
  @ExcelProperty("z轴")
  private String zWave;
  /**
   * 心率
   */
  @ExcelProperty("心率")
  private Short heartRate;
  /**
   * 呼吸率
   */
  @ExcelProperty("呼吸率")
  private Short respRate;
  /**
   * 血氧
   */
  @ExcelProperty("血氧")
  private Byte spo2;
  /**
   * 体位
   */
  @ExcelProperty("体位")
  private Integer gesture;
  /**
   * 能耗, 卡路里
   */
  @ExcelProperty("能耗")
  private Double energy;
  /**
   * 步数
   */
  @ExcelProperty("步数")
  private Short step;

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getEcgWave() {
    return ecgWave;
  }

  public void setEcgWave(String ecgWave) {
    this.ecgWave = ecgWave;
  }

  public String getPulseWave() {
    return pulseWave;
  }

  public void setPulseWave(String pulseWave) {
    this.pulseWave = pulseWave;
  }

  public String getRespWave() {
    return respWave;
  }

  public void setRespWave(String respWave) {
    this.respWave = respWave;
  }

  public String getAbdominalRespWave() {
    return abdominalRespWave;
  }

  public void setAbdominalRespWave(String abdominalRespWave) {
    this.abdominalRespWave = abdominalRespWave;
  }

  public String getxWave() {
    return xWave;
  }

  public void setxWave(String xWave) {
    this.xWave = xWave;
  }

  public String getyWave() {
    return yWave;
  }

  public void setyWave(String yWave) {
    this.yWave = yWave;
  }

  public String getzWave() {
    return zWave;
  }

  public void setzWave(String zWave) {
    this.zWave = zWave;
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
}
