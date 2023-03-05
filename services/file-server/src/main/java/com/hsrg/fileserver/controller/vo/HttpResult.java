package com.hsrg.fileserver.controller.vo;


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

  public static <T> HttpResult<T> succeed(T data) {
    return create(200, "SUCCESS", data);
  }

  public static <T> HttpResult<T> fail(String msg) {
    return fail(400, msg);
  }

  public static <T> HttpResult<T> fail(int code, String msg) {
    return create(code, msg, null);
  }

  private static <T> HttpResult<T> create(int code, String msg, T data) {
    return HttpResult.<T>builder()
        .code(code)
        .message(msg)
        .data(data)
        .build();
  }

  @ApiModelProperty("结果码")
  private int code;
  @ApiModelProperty("结果提示")
  private String message;
  @ApiModelProperty("数据")
  private T data;

}
