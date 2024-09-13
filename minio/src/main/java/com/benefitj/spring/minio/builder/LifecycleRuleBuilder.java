package com.benefitj.spring.minio.builder;


import io.minio.messages.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;


public class LifecycleRuleBuilder {

  public static LifecycleRuleBuilder builder() {
    return new LifecycleRuleBuilder();
  }

  Status status;
  AbortIncompleteMultipartUpload abortIncompleteMultipartUpload;
  Expiration expiration;
  RuleFilter filter;
  String id;
  NoncurrentVersionExpiration noncurrentVersionExpiration;
  NoncurrentVersionTransition noncurrentVersionTransition;
  Transition transition;

  public Status getStatus() {
    return status;
  }

  public LifecycleRuleBuilder setStatus(Status status) {
    this.status = status;
    return this;
  }

  public LifecycleRuleBuilder setStatus(boolean status) {
    return setStatus(status ? Status.ENABLED : Status.DISABLED);
  }

  public AbortIncompleteMultipartUpload getAbortIncompleteMultipartUpload() {
    return abortIncompleteMultipartUpload;
  }

  public LifecycleRuleBuilder setAbortIncompleteMultipartUpload(AbortIncompleteMultipartUpload abortIncompleteMultipartUpload) {
    this.abortIncompleteMultipartUpload = abortIncompleteMultipartUpload;
    return this;
  }

  public LifecycleRuleBuilder setAbortIncompleteMultipartUpload(int daysAfterInitiation) {
    return setAbortIncompleteMultipartUpload(new AbortIncompleteMultipartUpload(daysAfterInitiation));
  }

  public Expiration getExpiration() {
    return expiration;
  }

  public LifecycleRuleBuilder setExpiration(Expiration expiration) {
    this.expiration = expiration;
    return this;
  }

  public LifecycleRuleBuilder setExpiration(ZonedDateTime date, Integer days, Boolean expiredObjectDeleteMarker) {
    return setExpiration(new Expiration(date, days, expiredObjectDeleteMarker));
  }

  public RuleFilter getFilter() {
    return filter;
  }

  public LifecycleRuleBuilder setFilter(RuleFilter filter) {
    this.filter = filter;
    return this;
  }

  public LifecycleRuleBuilder setFilter(@Nullable AndOperator andOperator,
                                        @Nullable String prefix,
                                        @Nullable Tag tag) {
    return setFilter(new RuleFilter(andOperator, prefix, tag));
  }

  public String getId() {
    return id;
  }

  public LifecycleRuleBuilder setId(String id) {
    this.id = id;
    return this;
  }

  public NoncurrentVersionExpiration getNoncurrentVersionExpiration() {
    return noncurrentVersionExpiration;
  }

  public LifecycleRuleBuilder setNoncurrentVersionExpiration(NoncurrentVersionExpiration noncurrentVersionExpiration) {
    this.noncurrentVersionExpiration = noncurrentVersionExpiration;
    return this;
  }

  public LifecycleRuleBuilder setNoncurrentVersionExpiration(int noncurrentDays) {
    return setNoncurrentVersionExpiration(new NoncurrentVersionExpiration(noncurrentDays));
  }

  public NoncurrentVersionTransition getNoncurrentVersionTransition() {
    return noncurrentVersionTransition;
  }

  public LifecycleRuleBuilder setNoncurrentVersionTransition(NoncurrentVersionTransition noncurrentVersionTransition) {
    this.noncurrentVersionTransition = noncurrentVersionTransition;
    return this;
  }

  public LifecycleRuleBuilder setNoncurrentVersionTransition(int noncurrentDays, @Nonnull String storageClass) {
    return setNoncurrentVersionTransition(new NoncurrentVersionTransition(noncurrentDays, storageClass));
  }

  public Transition getTransition() {
    return transition;
  }

  public LifecycleRuleBuilder setTransition(Transition transition) {
    this.transition = transition;
    return this;
  }

  public LifecycleRuleBuilder setTransition(@Nullable ResponseDate date, @Nullable Integer days, @Nonnull String storageClass) {
    return setTransition(new Transition(date, days, storageClass));
  }

  public LifecycleRule toRule() {
    return new LifecycleRule(
        getStatus(),
        getAbortIncompleteMultipartUpload(),
        getExpiration(),
        getFilter(),
        getId(),
        getNoncurrentVersionExpiration(),
        getNoncurrentVersionTransition(),
        getTransition()
    );
  }

}
