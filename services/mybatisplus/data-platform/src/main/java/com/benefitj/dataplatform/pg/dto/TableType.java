package com.benefitj.dataplatform.pg.dto;


/**
 * 表类型
 */
public enum TableType {

  /**
   * The table is an ordinary table, or a table whose type isn't recognised by jOOQ (yet).
   *
   */
  BASE_TABLE("BASE TABLE"),

  /**
   * The table is a VIEW.
   *
   */
  VIEW("VIEW"),

  /**
   * The table is a MATERIALIZED VIEW.
   *
   */
  MATERIALIZED_VIEW("MATERIALIZED VIEW"),

  /**
   * The table is a TEMPORARY table.
   *
   */
  GLOBAL_TEMPORARY("GLOBAL TEMPORARY");

  private final String value;

  TableType(String v) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static TableType fromValue(String v) {
    for (TableType c: TableType.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }

  @Override
  public String toString() {
    switch (this) {
      case BASE_TABLE:
        return "BASE TABLE";
      case MATERIALIZED_VIEW:
        return "MATERIALIZED VIEW";
      case GLOBAL_TEMPORARY:
        return "GLOBAL TEMPORARY";
      default:
        return this.name();
    }
  }

}

