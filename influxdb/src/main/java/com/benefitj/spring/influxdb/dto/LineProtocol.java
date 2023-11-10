package com.benefitj.spring.influxdb.dto;

import com.benefitj.spring.influxdb.InfluxUtils;
import lombok.experimental.SuperBuilder;

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
   * TAG
   */
  private Map<String, String> tags;
  /**
   * 字段和值
   */
  private Map<String, Object> fields;

  public LineProtocol() {
  }

  public LineProtocol(String measurement, long time) {
    this(measurement, time, new LinkedHashMap<>(), new LinkedHashMap<>());
  }

  public LineProtocol(String measurement, long time, Map<String, String> tags, Map<String, Object> fields) {
    this.measurement = measurement;
    this.time = time;
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

  public long getTimeAsMilliSeconds() {
    return TimeUnit.NANOSECONDS.toMillis(this.getTime());
  }

  public long getTimeAsSeconds() {
    return TimeUnit.NANOSECONDS.toSeconds(this.getTime());
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
    return copy;
  }

}
