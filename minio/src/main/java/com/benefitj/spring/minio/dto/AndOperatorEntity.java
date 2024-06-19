package com.benefitj.spring.minio.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.minio.messages.AndOperator;

import java.util.Map;

public class AndOperatorEntity {

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private AndOperator raw;
  private String prefix;
  private Map<String, String> tags;

  public AndOperatorEntity() {
  }

  public AndOperatorEntity(AndOperator raw,
                           String prefix,
                           Map<String, String> tags) {
    this.raw = raw;
    this.prefix = prefix;
    this.tags = tags;
  }

  public AndOperator getRaw() {
    return raw;
  }

  public void setRaw(AndOperator raw) {
    this.raw = raw;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public Map<String, String> getTags() {
    return tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }

  public AndOperator toAndOperator() {
    return to(this);
  }

  public static AndOperatorEntity from(AndOperator src) {
    return src != null
        ? new AndOperatorEntity(src, src.prefix(), src.tags())
        : null;
  }

  public static AndOperator to(AndOperatorEntity src) {
    return src != null
        ? new AndOperator(src.getPrefix(), src.getTags())
        : null;
  }

}
