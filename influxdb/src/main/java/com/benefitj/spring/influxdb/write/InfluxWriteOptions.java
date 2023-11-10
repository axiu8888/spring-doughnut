package com.benefitj.spring.influxdb.write;

/**
 * 行协议文件写入配置
 */
public class InfluxWriteOptions {

  /**
   * 缓存大小(MB)，默认30MB
   */
  private Integer cacheSize = 30;
  /**
   * 延迟时长(秒)，默认5分钟
   */
  private Integer delay = 5 * 60;
  /**
   * 缓存目录：默认当前目录下的 ./tmp/influxdb
   */
  private String cacheDir = "./tmp/influxdb";
  /**
   * 是否自动上传，默认false
   */
  private boolean autoUpload = false;
  /**
   * 文件后缀，默认是 “.line”
   */
  private String suffix = ".line";

  public Integer getCacheSize() {
    return cacheSize;
  }

  public void setCacheSize(Integer cacheSize) {
    this.cacheSize = cacheSize;
  }

  public Integer getDelay() {
    return delay;
  }

  public void setDelay(Integer delay) {
    this.delay = delay;
  }

  public String getCacheDir() {
    return cacheDir;
  }

  public void setCacheDir(String cacheDir) {
    this.cacheDir = cacheDir;
  }

  public boolean isAutoUpload() {
    return autoUpload;
  }

  public void setAutoUpload(boolean autoUpload) {
    this.autoUpload = autoUpload;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

}
