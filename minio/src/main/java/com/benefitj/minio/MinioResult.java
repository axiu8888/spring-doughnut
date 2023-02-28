package com.benefitj.minio;

import com.alibaba.fastjson2.JSONObject;

public class MinioResult<T> {

  public static <T> MinioResult<T> succeed(T data) {
    return create(200, "SUCCESS", data);
  }

  public static <T> MinioResult<T> fail(String msg) {
    return create(400, msg, null);
  }

  public static <T> MinioResult<T> create(int code, String msg) {
    return create(code, msg, null);
  }

  public static <T> MinioResult<T> create(int code, String msg, T data) {
    return new MinioResult<>(code, msg, data);
  }

  public MinioResult() {
  }

  public MinioResult(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public MinioResult(int code, String message, T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  /**
   * 结果码
   */
  private int code;
  /**
   * 结果描述
   */
  private String message;
  /**
   * 对象
   */
  private T data;
  /**
   * 额外参数
   */
  private final JSONObject extras = new JSONObject();

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public JSONObject getExtras() {
    return extras;
  }

  public Object putExtra(String key, Object value) {
    return getExtras().put(key, value);
  }

  public <T> T getExtra(String key, Class<T> type) {
    return getExtras().getObject(key, type);
  }

  public boolean isSuccessful() {
    return getCode() / 200 == 1;
  }

  public MinioResult<T> handle(MinioHandler handler) {
    handler.accept(this);
    return this;
  }
}
