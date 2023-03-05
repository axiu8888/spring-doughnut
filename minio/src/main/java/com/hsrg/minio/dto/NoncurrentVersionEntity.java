package com.hsrg.minio.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.minio.messages.NoncurrentVersionExpiration;
import io.minio.messages.NoncurrentVersionTransition;

public class NoncurrentVersionEntity {

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private NoncurrentVersionExpiration raw;
  private int noncurrentDays;
  private String storageClass;

  public NoncurrentVersionEntity() {
  }

  public NoncurrentVersionEntity(NoncurrentVersionExpiration raw,
                                 int noncurrentDays,
                                 String storageClass) {
    this.raw = raw;
    this.noncurrentDays = noncurrentDays;
    this.storageClass = storageClass;
  }

  public NoncurrentVersionExpiration getRaw() {
    return raw;
  }

  public void setRaw(NoncurrentVersionExpiration raw) {
    this.raw = raw;
  }

  public int getNoncurrentDays() {
    return noncurrentDays;
  }

  public void setNoncurrentDays(int noncurrentDays) {
    this.noncurrentDays = noncurrentDays;
  }

  public String getStorageClass() {
    return storageClass;
  }

  public void setStorageClass(String storageClass) {
    this.storageClass = storageClass;
  }

  public static NoncurrentVersionEntity from(NoncurrentVersionExpiration src) {
    return src != null ? new NoncurrentVersionEntity(
        src,
        src.noncurrentDays(),
        src instanceof NoncurrentVersionTransition ? ((NoncurrentVersionTransition) src).storageClass() : null
    ) : null;
  }

  public static NoncurrentVersionTransition to(NoncurrentVersionEntity src) {
    return src != null ? new NoncurrentVersionTransition(
        src.getNoncurrentDays(),
        src.getStorageClass()
    ) : null;
  }

}
