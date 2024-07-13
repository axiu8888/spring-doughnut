package com.benefitj.spring.influxdb;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import okhttp3.logging.HttpLoggingInterceptor;

import java.time.Duration;

@SuperBuilder
@NoArgsConstructor
@Data
public class InfluxOptions {

  /**
   * InfluxDB连接路径
   */
  String url;
  /**
   * 用户名
   */
  String username;
  /**
   * 密码
   */
  String password;
  /**
   * 数据库
   */
  String database;
  /**
   * 存储策略，默认 autogen
   */
  @Builder.Default
  String retentionPolicy = "autogen";
  /**
   * 批量保存时的一致性策略，默认 {@link InfluxApi.ConsistencyLevel#ALL}
   */
  @Builder.Default
  InfluxApi.ConsistencyLevel consistencyLevel = InfluxApi.ConsistencyLevel.ALL;
  /**
   * 连接超时时长，默认5秒
   */
  @Builder.Default
  int connectTimeout = 5;
  /**
   * 读取超时时长，默认30秒
   */
  @Builder.Default
  int readTimeout = 30;
  /**
   * 写入超时时长，默认60秒
   */
  @Builder.Default
  int writeTimeout = 60;
  /**
   * gzip压缩
   */
  @Builder.Default
  boolean gzip = true;
  /**
   * 响应格式，默认JSON
   */
  @Builder.Default
  InfluxApi.ResponseFormat responseFormat = InfluxApi.ResponseFormat.JSON;
  /**
   * 日志等级
   */
  @Builder.Default
  HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.NONE;

  /**
   * 写入配置
   */
  @Builder.Default
  Writer writer = new Writer();

  @Builder.Default
  Api api = new Api();


  @SuperBuilder
  @NoArgsConstructor
  @Data
  public static class Writer {

    /**
     * 缓存大小(MB)，默认30MB
     */
    @Builder.Default
    Integer cacheSize = 30;
    /**
     * 延迟时长(秒)，默认5分钟
     */
    @Builder.Default
    Integer delay = 5 * 60;
    /**
     * 缓存目录：默认当前目录下的 ./lines
     */
    @Builder.Default
    String cacheDir = "./lines";
    /**
     * 是否自动上传，默认false
     */
    @Builder.Default
    boolean autoUpload = false;
    /**
     * 文件后缀，默认是 “.line”
     */
    @Builder.Default
    String suffix = ".line";
  }

  @SuperBuilder
  @NoArgsConstructor
  @Data
  public static class Api {
    /**
     * 缓存目录
     */
    @Builder.Default
    String cacheDir = "./tmp";
    /**
     * 缓存时长，默认30分钟
     */
    @Builder.Default
    Duration cacheDuration = Duration.ofMinutes(30);
    /**
     * 是否自动删除
     */
    @Builder.Default
    boolean autoDelete = true;
  }
}
