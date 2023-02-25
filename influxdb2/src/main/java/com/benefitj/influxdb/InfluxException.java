package com.benefitj.influxdb;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ImmutableMapValue;
import org.msgpack.value.impl.ImmutableStringValueImpl;

import java.io.InputStream;

/**
 * A wrapper for various exceptions caused while interacting with InfluxDB.
 *
 * @author Simon Legner
 */
public class InfluxException extends RuntimeException {

  public InfluxException(final String message) {
    super(message);
  }

  public InfluxException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InfluxException(final Throwable cause) {
    super(cause);
  }

  /**
   * @return true if the operation may succeed if repeated, false otherwise.
   */
  public boolean isRetryWorth() {
    return true;
  }

  /* See https://github.com/influxdata/influxdb/blob/master/tsdb/shard.go */
  static final String FIELD_TYPE_CONFLICT_ERROR = "field type conflict";
  /* See https://github.com/influxdata/influxdb/blob/master/coordinator/points_writer.go */
  static final String POINTS_BEYOND_RETENTION_POLICY_ERROR = "points beyond retention policy";
  /* See https://github.com/influxdata/influxdb/blob/master/models/points.go */
  static final String UNABLE_TO_PARSE_ERROR = "unable to parse";
  /* See https://github.com/influxdata/telegraf/blob/master/plugins/outputs/influxdb/influxdb.go */
  static final String HINTED_HAND_OFF_QUEUE_NOT_EMPTY_ERROR = "hinted handoff queue not empty";
  /* See https://github.com/influxdata/influxdb/blob/master/tsdb/engine/tsm1/cache.go */
  static final String CACHE_MAX_MEMORY_SIZE_EXCEEDED_ERROR = "cache-max-memory-size exceeded";
  /* For all messages below see https://github.com/influxdata/influxdb/blob/master/services/httpd/handler.go */
  static final String DATABASE_NOT_FOUND_ERROR = "database not found";
  static final String USER_REQUIRED_ERROR = "user is required to write to database";
  static final String USER_NOT_AUTHORIZED_ERROR = "user is not authorized to write to database";
  static final String AUTHORIZATION_FAILED_ERROR = "authorization failed";
  static final String USERNAME_REQUIRED_ERROR = "username required";

  public static final class DatabaseNotFoundException extends InfluxException {
    private DatabaseNotFoundException(final String message) {
      super(message);
    }

    public boolean isRetryWorth() {
      return false;
    }
  }

  public static final class HintedHandOffQueueNotEmptyException extends InfluxException {
    private HintedHandOffQueueNotEmptyException(final String message) {
      super(message);
    }

    public boolean isRetryWorth() {
      return false;
    }
  }

  public static final class UnableToParseException extends InfluxException {
    private UnableToParseException(final String message) {
      super(message);
    }

    public boolean isRetryWorth() {
      return false;
    }
  }

  public static final class FieldTypeConflictException extends InfluxException {
    private FieldTypeConflictException(final String message) {
      super(message);
    }

    public boolean isRetryWorth() {
      return false;
    }
  }

  public static final class PointsBeyondRetentionPolicyException extends InfluxException {
    private PointsBeyondRetentionPolicyException(final String message) {
      super(message);
    }

    public boolean isRetryWorth() {
      return false;
    }
  }

  public static final class CacheMaxMemorySizeExceededException extends InfluxException {
    private CacheMaxMemorySizeExceededException(final String message) {
      super(message);
    }

    public boolean isRetryWorth() {
      return true;
    }
  }

  public static final class RetryBufferOverrunException extends InfluxException {
    public RetryBufferOverrunException(final String message) {
      super(message);
    }

    public boolean isRetryWorth() {
      return false;
    }
  }

  public static final class AuthorizationFailedException extends InfluxException {
    public AuthorizationFailedException(final String message) {
      super(message);
    }

    public boolean isRetryWorth() {
      return false;
    }
  }

  private static InfluxException buildExceptionFromErrorMessage(final String errorMessage) {
    if (errorMessage.contains(DATABASE_NOT_FOUND_ERROR)) {
      return new DatabaseNotFoundException(errorMessage);
    }
    if (errorMessage.contains(POINTS_BEYOND_RETENTION_POLICY_ERROR)) {
      return new PointsBeyondRetentionPolicyException(errorMessage);
    }
    if (errorMessage.contains(FIELD_TYPE_CONFLICT_ERROR)) {
      return new FieldTypeConflictException(errorMessage);
    }
    if (errorMessage.contains(UNABLE_TO_PARSE_ERROR)) {
      return new UnableToParseException(errorMessage);
    }
    if (errorMessage.contains(HINTED_HAND_OFF_QUEUE_NOT_EMPTY_ERROR)) {
      return new HintedHandOffQueueNotEmptyException(errorMessage);
    }
    if (errorMessage.contains(CACHE_MAX_MEMORY_SIZE_EXCEEDED_ERROR)) {
      return new CacheMaxMemorySizeExceededException(errorMessage);
    }
    if (errorMessage.contains(USER_REQUIRED_ERROR)
            || errorMessage.contains(USER_NOT_AUTHORIZED_ERROR)
            || errorMessage.contains(AUTHORIZATION_FAILED_ERROR)
            || errorMessage.contains(USERNAME_REQUIRED_ERROR)) {
      return new AuthorizationFailedException(errorMessage);
    }
    return new InfluxException(errorMessage);
  }

  private static class ErrorMessage {
    public String error;
  }

  public static InfluxException buildExceptionForErrorState(final String errorBody) {
    try {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<ErrorMessage> adapter = moshi.adapter(ErrorMessage.class).lenient();
      ErrorMessage errorMessage = adapter.fromJson(errorBody);
      return InfluxException.buildExceptionFromErrorMessage(errorMessage.error);
    } catch (Exception e) {
      return new InfluxException(errorBody);
    }
  }

  /**
   * Create corresponding InfluxDBException from the message pack error body.
   * @param messagePackErrorBody
   *          the error body if any
   * @return the Exception
   */
  public static InfluxException buildExceptionForErrorState(final InputStream messagePackErrorBody) {
    try {
      MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(messagePackErrorBody);
      ImmutableMapValue mapVal = (ImmutableMapValue) unpacker.unpackValue();
      return InfluxException.buildExceptionFromErrorMessage(
          mapVal.map().get(new ImmutableStringValueImpl("error")).toString());
    } catch (Exception e) {
      return new InfluxException(e);
    }
  }
}
