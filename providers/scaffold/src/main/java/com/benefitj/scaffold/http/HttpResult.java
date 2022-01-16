package com.benefitj.scaffold.http;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("请求结果")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class HttpResult<T> {

  /**
   * 状态码
   */
  @ApiModelProperty("结果码")
  private int code;
  /**
   * 信息
   */
  @ApiModelProperty("信息")
  private String msg;
  /**
   * 数据
   */
  @ApiModelProperty("数据")
  private T data;

  /**
   * 成功
   *
   * @param <T> 类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> succeed() {
    return succeed(null);
  }

  /**
   * 成功
   *
   * @param data 数据
   * @param <T>  类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> succeed(T data) {
    return create(200, "success", data);
  }

  /**
   * 成功
   *
   * @param code 结果码
   * @param msg  消息
   * @param <T>  类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> succeed(int code, String msg) {
    return create(code, msg, null);
  }

  /**
   * 失败
   *
   * @param <T> 类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> fail() {
    return fail("failure");
  }

  /**
   * 失败
   *
   * @param msg 错误信息
   * @param <T> 类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> fail(String msg) {
    return fail(400, msg);
  }

  /**
   * 失败
   *
   * @param code 结果码
   * @param msg  消息
   * @param <T>  类型
   * @return 返回Http结果
   */
  public static <T> HttpResult<T> fail(int code, String msg) {
    return create(code, msg, null);
  }

  /**
   * 创建HttpResult
   *
   * @param code 状态码
   * @param msg  消息
   * @param <T>  类型
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
   * @param <T>  类型
   * @return 返回结果
   */
  public static <T> HttpResult<T> create(int code, String msg, T data) {
    return (HttpResult<T>) builder()
        .code(code)
        .msg(msg)
        .data(data)
        .build();
  }

}
