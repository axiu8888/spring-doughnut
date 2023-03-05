package com.hsrg.minio.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.minio.messages.AbortIncompleteMultipartUpload;
import io.minio.messages.AndOperator;
import io.minio.messages.LifecycleRule;
import io.minio.messages.Status;

public class LifecycleRuleEntity {

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private LifecycleRule raw;
  private Integer daysAfterInitiation;
  private DateDaysEntity expiration;
  private RuleFilterEntity filter;
  private String id;
  private NoncurrentVersionEntity noncurrentVersionExpiration;
  private NoncurrentVersionEntity noncurrentVersionTransition;
  private Status status;
  private DateDaysEntity transition;

  public LifecycleRuleEntity() {
  }

  public LifecycleRuleEntity(LifecycleRule raw,
                             Integer daysAfterInitiation,
                             DateDaysEntity expiration,
                             RuleFilterEntity filter,
                             String id,
                             NoncurrentVersionEntity noncurrentVersionExpiration,
                             NoncurrentVersionEntity noncurrentVersionTransition,
                             Status status,
                             DateDaysEntity transition) {
    this.raw = raw;
    this.daysAfterInitiation = daysAfterInitiation;
    this.expiration = expiration;
    this.filter = filter;
    this.id = id;
    this.noncurrentVersionExpiration = noncurrentVersionExpiration;
    this.noncurrentVersionTransition = noncurrentVersionTransition;
    this.status = status;
    this.transition = transition;
  }

  public LifecycleRule getRaw() {
    return raw;
  }

  public void setRaw(LifecycleRule raw) {
    this.raw = raw;
  }

  public Integer getDaysAfterInitiation() {
    return daysAfterInitiation;
  }

  public void setDaysAfterInitiation(Integer daysAfterInitiation) {
    this.daysAfterInitiation = daysAfterInitiation;
  }

  public DateDaysEntity getExpiration() {
    return expiration;
  }

  public void setExpiration(DateDaysEntity expiration) {
    this.expiration = expiration;
  }

  public RuleFilterEntity getFilter() {
    return filter;
  }

  public void setFilter(RuleFilterEntity filter) {
    this.filter = filter;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NoncurrentVersionEntity getNoncurrentVersionExpiration() {
    return noncurrentVersionExpiration;
  }

  public void setNoncurrentVersionExpiration(NoncurrentVersionEntity noncurrentVersionExpiration) {
    this.noncurrentVersionExpiration = noncurrentVersionExpiration;
  }

  public NoncurrentVersionEntity getNoncurrentVersionTransition() {
    return noncurrentVersionTransition;
  }

  public void setNoncurrentVersionTransition(NoncurrentVersionEntity noncurrentVersionTransition) {
    this.noncurrentVersionTransition = noncurrentVersionTransition;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public DateDaysEntity getTransition() {
    return transition;
  }

  public void setTransition(DateDaysEntity transition) {
    this.transition = transition;
  }

  public LifecycleRule toLifecycleRule() {
    return to(this);
  }

  public static LifecycleRuleEntity from(LifecycleRule src) {
    return src != null ? new LifecycleRuleEntity(
        src,
        src.abortIncompleteMultipartUpload() != null ? src.abortIncompleteMultipartUpload().daysAfterInitiation() : null,
        DateDaysEntity.from(src.expiration()),
        RuleFilterEntity.from(src.filter()),
        src.id(),
        NoncurrentVersionEntity.from(src.noncurrentVersionExpiration()),
        NoncurrentVersionEntity.from(src.noncurrentVersionTransition()),
        src.status(),
        DateDaysEntity.from(src.transition())
    ) : null;
  }

  public static LifecycleRule to(LifecycleRuleEntity rule) {
    return rule != null ? new LifecycleRule(
        rule.getStatus(),
        rule.getDaysAfterInitiation() != null ? new AbortIncompleteMultipartUpload(rule.getDaysAfterInitiation()) : null,
        DateDaysEntity.toExpiration(rule.getExpiration()),
        RuleFilterEntity.to(rule.getFilter()),
        rule.getId(),
        NoncurrentVersionEntity.to(rule.getNoncurrentVersionExpiration()),
        NoncurrentVersionEntity.to(rule.getNoncurrentVersionTransition()),
        DateDaysEntity.toTransition(rule.getTransition())
    ) : null;
  }

}
