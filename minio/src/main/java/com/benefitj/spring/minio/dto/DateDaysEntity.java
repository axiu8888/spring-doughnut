package com.benefitj.spring.minio.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.minio.messages.DateDays;
import io.minio.messages.Expiration;
import io.minio.messages.ResponseDate;
import io.minio.messages.Transition;

import java.time.ZonedDateTime;

public class DateDaysEntity {

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private DateDays raw;
  protected ZonedDateTime date;

  protected Integer days;

  private String storageClass;
  private Boolean expiredObjectDeleteMarker;

  public DateDaysEntity() {
  }

  public DateDaysEntity(DateDays raw,
                        ZonedDateTime date,
                        Integer days,
                        Boolean expiredObjectDeleteMarker,
                        String storageClass) {
    this.raw = raw;
    this.date = date;
    this.days = days;
    this.expiredObjectDeleteMarker = expiredObjectDeleteMarker;
    this.storageClass = storageClass;
  }

  public DateDays getRaw() {
    return raw;
  }

  public void setRaw(DateDays raw) {
    this.raw = raw;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public Integer getDays() {
    return days;
  }

  public void setDays(Integer days) {
    this.days = days;
  }

  public Boolean getExpiredObjectDeleteMarker() {
    return expiredObjectDeleteMarker;
  }

  public void setExpiredObjectDeleteMarker(Boolean expiredObjectDeleteMarker) {
    this.expiredObjectDeleteMarker = expiredObjectDeleteMarker;
  }

  public String getStorageClass() {
    return storageClass;
  }

  public void setStorageClass(String storageClass) {
    this.storageClass = storageClass;
  }

  public Expiration toExpiration() {
    return toExpiration(this);
  }

  public Transition toTransition() {
    return toTransition(this);
  }

  public static DateDaysEntity from(DateDays src) {
    return src != null ? new DateDaysEntity(
        src,
        src.date(),
        src.days(),
        src instanceof Expiration ? ((Expiration) src).expiredObjectDeleteMarker() : null,
        src instanceof Transition ? ((Transition) src).storageClass() : null
    ) : null;
  }

  public static Expiration toExpiration(DateDaysEntity src) {
    return src != null ? new Expiration(
        new ResponseDate(src.date),
        src.days,
        src.expiredObjectDeleteMarker
    ) : null;
  }

  public static Transition toTransition(DateDaysEntity src) {
    return src != null ? new Transition(
        new ResponseDate(src.date),
        src.days,
        src.storageClass
    ) : null;
  }
}
