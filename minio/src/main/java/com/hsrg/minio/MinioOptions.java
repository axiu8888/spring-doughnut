package com.hsrg.minio;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * MinIO配置
 */
public class MinioOptions {

  /**
   * 服务端：https://127.0.0.1
   */
  private String endpoint;
  /**
   * 端口：9001、9006
   */
  private Integer port;
  /**
   * 访问域
   */
  private String region;
  /**
   * 访问秘钥：Ei4H6nMGYs9NcISp
   */
  private String accessKey;
  /**
   * 安全秘钥：CYMQoY59q3NCgp4fbYnmiFnZNzXjs76T
   */
  private String secretKey;
  /**
   * 是否自动创建桶，如果桶不存在时
   */
  private boolean autoMakeBucket = false;
  /**
   * 日志等级：NONE、BODY、BASIC
   */
  private HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.NONE;

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public boolean isAutoMakeBucket() {
    return autoMakeBucket;
  }

  public void setAutoMakeBucket(boolean autoMakeBucket) {
    this.autoMakeBucket = autoMakeBucket;
  }

  public HttpLoggingInterceptor.Level getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(HttpLoggingInterceptor.Level logLevel) {
    this.logLevel = logLevel;
  }
}
