package com.benefitj.spring.influxdb.template;

import org.influxdb.InfluxDB;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * InfluxDB的配置
 */
@ConfigurationProperties(prefix = "spring.influxdb")
public class InfluxProperty {

  /**
   * InfluxDB连接路径
   */
  private String url;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * 数据库
   */
  private String database;
  /**
   * 存储策略，默认 autogen
   */
  private String retentionPolicy = "autogen";
  /**
   * 批量保存时的一致性策略，默认 {@link InfluxDB.ConsistencyLevel#ALL}
   */
  private InfluxDB.ConsistencyLevel consistencyLevel = InfluxDB.ConsistencyLevel.ALL;
  /**
   * 连接超时时长，默认5秒
   */
  private int connectTimeout = 5;
  /**
   * 读取超时时长，默认30秒
   */
  private int readTimeout = 30;
  /**
   * 写入超时时长，默认60秒
   */
  private int writeTimeout = 60;
  /**
   * gzip压缩
   */
  private boolean gzip = true;
  /**
   * 是否支持批处理
   */
  private boolean enableBatch = true;
  /**
   * 批处理的响应数
   */
  private int batchActions = 100000;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getRetentionPolicy() {
    return retentionPolicy;
  }

  public void setRetentionPolicy(String retentionPolicy) {
    this.retentionPolicy = retentionPolicy;
  }

  public InfluxDB.ConsistencyLevel getConsistencyLevel() {
    return consistencyLevel;
  }

  public void setConsistencyLevel(InfluxDB.ConsistencyLevel consistencyLevel) {
    this.consistencyLevel = consistencyLevel;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }

  public int getWriteTimeout() {
    return writeTimeout;
  }

  public void setWriteTimeout(int writeTimeout) {
    this.writeTimeout = writeTimeout;
  }

  public boolean isGzip() {
    return gzip;
  }

  public void setGzip(boolean gzip) {
    this.gzip = gzip;
  }

  public boolean isEnableBatch() {
    return enableBatch;
  }

  public void setEnableBatch(boolean enableBatch) {
    this.enableBatch = enableBatch;
  }

  public int getBatchActions() {
    return batchActions;
  }

  public void setBatchActions(int batchActions) {
    this.batchActions = batchActions;
  }
}
