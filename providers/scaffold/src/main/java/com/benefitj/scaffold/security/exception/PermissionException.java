package com.benefitj.scaffold.security.exception;

/**
 * 权限异常
 */
public class PermissionException extends RuntimeException {

  public PermissionException(String message) {
    super(message);
  }

  public PermissionException(String message, Throwable cause) {
    super(message, cause);
  }

  public PermissionException(Throwable cause) {
    super(cause);
  }
}
