package com.benefitj.spring.mvc.query;

import java.util.Locale;
import java.util.Optional;

/**
 * 排序方向
 */
public enum OrderDirection {
  ASC, DESC;

  /**
   * Returns the {@link OrderDirection} enum for the given {@link String} value.
   *
   * @param value
   * @return
   * @throws IllegalArgumentException in case the given value cannot be parsed into an enum value.
   */
  public static OrderDirection fromString(String value) {
    try {
      return OrderDirection.valueOf(value.toUpperCase(Locale.getDefault()));
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format(
          "Invalid value '%s' for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value), e);
    }
  }

  /**
   * Returns the {@link OrderDirection} enum for the given {@link String} or null if it cannot be parsed into an enum
   * value.
   *
   * @param value
   * @return
   */
  public static Optional<OrderDirection> fromOptionalString(String value) {

    try {
      return Optional.of(fromString(value));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  /**
   * Returns whether the direction is ascending.
   *
   * @return
   * @since 1.13
   */
  public boolean isAscending() {
    return this.equals(ASC);
  }

  /**
   * Returns whether the direction is descending.
   *
   * @return
   * @since 1.13
   */
  public boolean isDescending() {
    return this.equals(DESC);
  }
}
