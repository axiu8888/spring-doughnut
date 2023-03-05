package com.hsrg.minio.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.minio.messages.RuleFilter;

public class RuleFilterEntity {

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private RuleFilter raw;
  private AndOperatorEntity andOperator;
  private String prefix;
  private TagEntity tag;

  public RuleFilterEntity() {
  }

  public RuleFilterEntity(RuleFilter raw,
                          AndOperatorEntity andOperator,
                          String prefix,
                          TagEntity tag) {
    this.raw = raw;
    this.andOperator = andOperator;
    this.prefix = prefix;
    this.tag = tag;
  }

  public RuleFilter getRaw() {
    return raw;
  }

  public void setRaw(RuleFilter raw) {
    this.raw = raw;
  }

  public AndOperatorEntity getAndOperator() {
    return andOperator;
  }

  public void setAndOperator(AndOperatorEntity andOperator) {
    this.andOperator = andOperator;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public TagEntity getTag() {
    return tag;
  }

  public void setTag(TagEntity tag) {
    this.tag = tag;
  }

  public static RuleFilterEntity from(RuleFilter src) {
    return new RuleFilterEntity(
        src,
        AndOperatorEntity.from(src.andOperator()),
        src.prefix(),
        TagEntity.from(src.tag())
    );
  }

  public static RuleFilter to(RuleFilterEntity filter) {
    return new RuleFilter(
        AndOperatorEntity.to(filter.getAndOperator()),
        filter.getPrefix(),
        TagEntity.to(filter.getTag())
    );
  }

}
