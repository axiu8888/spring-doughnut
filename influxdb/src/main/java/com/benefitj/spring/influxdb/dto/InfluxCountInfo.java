package com.benefitj.spring.influxdb.dto;

/**
 * 统计信息
 */
public class InfluxCountInfo {

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

  public InfluxCountInfo() {
  }

  public InfluxCountInfo(long count, Long startTime, Long endTime) {
    this.count = count;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public InfluxCountInfo(String error) {
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
