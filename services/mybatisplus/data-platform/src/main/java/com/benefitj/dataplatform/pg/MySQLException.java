package com.benefitj.dataplatform.pg;

public class MySQLException extends RuntimeException {

  public MySQLException() {
  }

  public MySQLException(String message) {
    super(message);
  }

  public MySQLException(String message, Throwable cause) {
    super(message, cause);
  }

  public MySQLException(Throwable cause) {
    super(cause);
  }

  public MySQLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
