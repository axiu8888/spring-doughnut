package com.hsrg.minio.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.benefitj.core.ReflectUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.minio.messages.Owner;

public class OwnerEntity {

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private Owner raw;

  private String id;

  private String displayName;

  public OwnerEntity() {
  }

  public OwnerEntity(Owner raw, String id, String displayName) {
    this.raw = raw;
    this.id = id;
    this.displayName = displayName;
  }

  public Owner getRaw() {
    return raw;
  }

  public void setRaw(Owner raw) {
    this.raw = raw;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Owner toOwner() {
    return to(this);
  }

  public static OwnerEntity from(Owner src) {
    return src != null ? new OwnerEntity(
        src,
        src.id(),
        src.displayName()
    ) : null;
  }

  public static Owner to(OwnerEntity src) {
    Owner owner = src != null ? new Owner() : null;
    if (owner != null) {
      ReflectUtils.setFieldValue(owner, src.getId(), f -> f.getName().equals("id"));
      ReflectUtils.setFieldValue(owner, src.getDisplayName(), f -> f.getName().equals("displayName"));
    }
    return owner;
  }

}
