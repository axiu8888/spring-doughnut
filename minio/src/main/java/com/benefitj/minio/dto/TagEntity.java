package com.benefitj.minio.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.minio.messages.Tag;

public class TagEntity {

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private Tag raw;
  private String key;
  private String value;

  public TagEntity() {
  }

  public TagEntity(Tag raw,
                   String key,
                   String value) {
    this.raw = raw;
    this.key = key;
    this.value = value;
  }

  public Tag getRaw() {
    return raw;
  }

  public void setRaw(Tag raw) {
    this.raw = raw;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Tag toTag() {
    return to(this);
  }

  public static TagEntity from(Tag src) {
    return src != null
        ? new TagEntity(src, src.key(), src.value())
        : null;
  }

  public static Tag to(TagEntity tag) {
    return tag != null ? new Tag(tag.getKey(), tag.getValue()) : null;
  }

}
