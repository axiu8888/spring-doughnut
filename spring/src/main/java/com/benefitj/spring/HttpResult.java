package com.benefitj.spring;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Http的结果
 *
 * @param <T>
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class HttpResult<T> {

  /**
   * 成功
   *
   * @param <T> 类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> success() {
    return success(null);
  }

  /**
   * 成功
   *
   * @param data 数据
   * @param <T>  类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> success(T data) {
    return create(200, "success", data);
  }

  /**
   * 成功
   *
   * @param msg  消息
   * @param data 数据
   * @param <T>  类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> success(String msg, T data) {
    return create(200, msg, data);
  }

  /**
   * 失败
   *
   * @param <T> 类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> failure() {
    return failure(null);
  }

  /**
   * 失败
   *
   * @param msg 消息
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> failure(String msg) {
    return failure(400, msg);
  }

  /**
   * 失败
   *
   * @param code 结果码
   * @param msg  消息
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> failure(int code, String msg) {
    return create(code, msg, null);
  }

  /**
   * 创建HttpResult
   *
   * @param code 状态码
   * @param msg  消息
   * @return 返回结果
   */
  public static <T> HttpResult<T> create(int code, String msg) {
    return create(code, msg, null);
  }

  /**
   * 创建HttpResult
   *
   * @param code 状态码
   * @param msg  消息
   * @param data 数据
   * @return 返回结果
   */
  public static <T> HttpResult<T> create(int code, String msg, T data) {
    return HttpResult.<T>builder()
        .code(code)
        .msg(msg)
        .data(data)
        .build();
  }

  /**
   * 当前时间戳
   */
  @Builder.Default
  long timestamp = System.currentTimeMillis();
  /**
   * 状态码
   */
  int code;
  /**
   * 信息
   */
  String msg;
  /**
   * 数据
   */
  T data;

}
