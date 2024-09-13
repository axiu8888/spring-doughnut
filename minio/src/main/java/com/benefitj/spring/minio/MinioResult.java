package com.benefitj.spring.minio;

import java.util.function.BiConsumer;

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
   * 异常
   */
  private Throwable error;

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

  public Throwable getError() {
    return error;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  public boolean isSuccessful() {
    return getCode() / 200 == 1;
  }

  public MinioResult handle(MinioHandler<T> handler) {
    handler.accept(this);
    return this;
  }

  public Promise<T> promise() {
    return new Promise<>(this);
  }

  public static class Promise<T> {

    final MinioResult<T> result;

    public Promise(MinioResult<T> result) {
      this.result = result;
    }

    public Promise<T> then(BiConsumer<MinioResult<T>, T> consumer) {
      if (result.isSuccessful())
        consumer.accept(result, result.getData());
      return this;
    }

    public Promise<T> error(BiConsumer<MinioResult<T>, Throwable> consumer) {
      if (!result.isSuccessful())
        consumer.accept(result, result.getError());
      return this;
    }

  }

}
