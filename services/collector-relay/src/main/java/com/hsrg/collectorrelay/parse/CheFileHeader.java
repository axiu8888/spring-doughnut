package com.hsrg.collectorrelay.parse;

/**
 * The type Che file header.
 */
public class CheFileHeader {
  /**
   * 原始数据
   */
  private volatile byte[] raw;
  /**
   * 产品名称: SensEcho
   */
  private String productName;
  /**
   * 产品型号: 5A4.0
   */
  private String productType;
  /**
   * 协议版本号: V1.0.0
   */
  private String version;
  /**
   * 固件版本号: V1.0.0
   */
  private String firmwareVersion;
  /**
   * 硬件版本号: V1.0.0
   */
  private String hardwareVersion;
  /**
   * 设备型号 + ID
   */
  private String deviceId;
  /**
   * 呼吸信息
   */
  private String respInfo;
  /**
   * 心电信息
   */
  private String ecgInfo;
  /**
   * 三轴信息
   */
  private String xyzInfo;
  /**
   * 血氧信息
   */
  private String spo2Info;
  /**
   * 原始数据
   */
  private String original;

  public byte[] getRaw() {
    return raw;
  }

  public void setRaw(byte[] raw) {
    this.raw = raw;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getFirmwareVersion() {
    return firmwareVersion;
  }

  public void setFirmwareVersion(String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  public String getHardwareVersion() {
    return hardwareVersion;
  }

  public void setHardwareVersion(String hardwareVersion) {
    this.hardwareVersion = hardwareVersion;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getRespInfo() {
    return respInfo;
  }

  public void setRespInfo(String respInfo) {
    this.respInfo = respInfo;
  }

  public String getEcgInfo() {
    return ecgInfo;
  }

  public void setEcgInfo(String ecgInfo) {
    this.ecgInfo = ecgInfo;
  }

  public String getXyzInfo() {
    return xyzInfo;
  }

  public void setXyzInfo(String xyzInfo) {
    this.xyzInfo = xyzInfo;
  }

  public String getSpo2Info() {
    return spo2Info;
  }

  public void setSpo2Info(String spo2Info) {
    this.spo2Info = spo2Info;
  }

  public String getOriginal() {
    return original;
  }

  public void setOriginal(String original) {
    this.original = original;
  }
}