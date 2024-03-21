package com.benefitj.spring.influxdb.dto;

import com.benefitj.spring.influxdb.InfluxUtils;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuperBuilder
public class LineProtocol {

  /**
   * 表名
   */
  private String measurement;
  /**
   * 时间戳
   */
  private long time;
  /**
   * 时间单位
   */
  @Builder.Default
  private TimeUnit timeUnit = TimeUnit.NANOSECONDS;
  /**
   * TAG
   */
  private Map<String, String> tags;
  /**
   * 字段和值
   */
  private Map<String, Object> fields;

  public LineProtocol() {
    this(null, 0L, TimeUnit.NANOSECONDS);
  }

  public LineProtocol(String measurement, long time, TimeUnit timeUnit) {
    this(measurement, time, timeUnit, new HashMap<>(), new HashMap<>());
  }

  public LineProtocol(String measurement, long time, TimeUnit timeUnit, Map<String, String> tags, Map<String, Object> fields) {
    this.measurement = measurement;
    this.time = time;
    this.timeUnit = timeUnit;
    this.tags = tags;
    this.fields = fields;
  }

  public String getMeasurement() {
    return measurement;
  }

  public void setMeasurement(String measurement) {
    this.measurement = measurement;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  public void setTimeUnit(TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }

  public Map<String, Object> getFields() {
    return fields;
  }

  public void setFields(Map<String, Object> values) {
    this.fields = values;
  }

  public Point.Builder toPointBuilder() {
    return InfluxUtils.toPointBuilder(this);
  }

  public Point toPoint() {
    return InfluxUtils.toPoint(this);
  }

  public String toLineProtocol() {
    return toPoint().lineProtocol();
  }

  public <T> T toPoJo(Class<T> type) {
    return InfluxUtils.toPoJo(type, this);
  }

  public LineProtocol copy() {
    LineProtocol copy = new LineProtocol();
    copy.setMeasurement(getMeasurement());
    copy.setTags(new LinkedHashMap<>(getTags()));
    copy.setFields(new LinkedHashMap<>(getFields()));
    copy.setTime(getTime());
    copy.setTimeUnit(getTimeUnit());
    return copy;
  }

}
