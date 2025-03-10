package com.benefitj.dataplatform.utils;

public class PSQLException extends RuntimeException {

  public PSQLException() {
  }

  public PSQLException(String message) {
    super(message);
  }

  public PSQLException(String message, Throwable cause) {
    super(message, cause);
  }

  public PSQLException(Throwable cause) {
    super(cause);
  }

  public PSQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
