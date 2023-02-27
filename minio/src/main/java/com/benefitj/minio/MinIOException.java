package com.benefitj.minio;

public class MinIOException extends RuntimeException {

  public MinIOException() {
  }

  public MinIOException(String message) {
    super(message);
  }

  public MinIOException(String message, Throwable cause) {
    super(message, cause);
  }

  public MinIOException(Throwable cause) {
    super(cause);
  }

  public MinIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
