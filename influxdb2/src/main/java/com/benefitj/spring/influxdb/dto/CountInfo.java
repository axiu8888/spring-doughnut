package com.benefitj.spring.influxdb.dto;

/**
 * 统计信息
 */
public class CountInfo {

  /**
   * SQL语句
   */
  private String sql;
  /**
   * 条数
   */
  private long count = 0L;
  /**
   * 开始时间
   */
  private Long startTime;
  /**
   * 结束时间
   */
  private Long endTime;
  /**
   * 错误信息
   */
  private String error;

  public CountInfo() {
  }

  public CountInfo(String sql, long count, Long startTime, Long endTime) {
    this.sql = sql;
    this.count = count;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public CountInfo(String error) {
    this.error = error;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public boolean hasError() {
    return getError() != null;
  }

}
