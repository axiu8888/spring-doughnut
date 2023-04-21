package com.benefitj.minio.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.benefitj.core.ReflectUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.minio.messages.*;

import java.time.ZonedDateTime;
import java.util.Map;

public class ItemEntity {

  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  private Item raw;

  private String etag; // except DeleteMarker

  private String objectName;

  private ZonedDateTime lastModified;

  private OwnerEntity owner;

  private long size; // except DeleteMarker

  private String storageClass; // except DeleteMarker, not in case of MinIO server.

  private boolean isDeleteMarker;

  private boolean isLatest; // except ListObjects V1

  private String versionId; // except ListObjects V1

  private Map<String, String> userMetadata;

  private boolean isDir = false;

  public ItemEntity() {
  }

  public ItemEntity(Item raw,
                    String etag,
                    String objectName,
                    ZonedDateTime lastModified,
                    OwnerEntity owner,
                    long size,
                    String storageClass,
                    boolean isDeleteMarker,
                    boolean isLatest,
                    String versionId,
                    Map<String, String> userMetadata,
                    boolean isDir) {
    this.raw = raw;
    this.etag = etag;
    this.objectName = objectName;
    this.lastModified = lastModified;
    this.owner = owner;
    this.size = size;
    this.storageClass = storageClass;
    this.isDeleteMarker = isDeleteMarker;
    this.isLatest = isLatest;
    this.versionId = versionId;
    this.userMetadata = userMetadata;
    this.isDir = isDir;
  }

  public Item getRaw() {
    return raw;
  }

  public void setRaw(Item raw) {
    this.raw = raw;
  }

  public String getEtag() {
    return etag;
  }

  public void setEtag(String etag) {
    this.etag = etag;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public ZonedDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(ZonedDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public OwnerEntity getOwner() {
    return owner;
  }

  public void setOwner(OwnerEntity owner) {
    this.owner = owner;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getStorageClass() {
    return storageClass;
  }

  public void setStorageClass(String storageClass) {
    this.storageClass = storageClass;
  }

  public boolean isDeleteMarker() {
    return isDeleteMarker;
  }

  public void setDeleteMarker(boolean deleteMarker) {
    isDeleteMarker = deleteMarker;
  }

  public boolean isLatest() {
    return isLatest;
  }

  public void setLatest(boolean latest) {
    isLatest = latest;
  }

  public String getVersionId() {
    return versionId;
  }

  public void setVersionId(String versionId) {
    this.versionId = versionId;
  }

  public Map<String, String> getUserMetadata() {
    return userMetadata;
  }

  public void setUserMetadata(Map<String, String> userMetadata) {
    this.userMetadata = userMetadata;
  }

  public boolean isDir() {
    return isDir;
  }

  public void setDir(boolean dir) {
    isDir = dir;
  }

  public Item toDeleteMarker() {
    return toDeleteMarker(this);
  }

  public Item toVersion() {
    return toVersion(this);
  }

  public Item toContents() {
    return toContents(this);
  }

  public static ItemEntity from(Item src) {
    return src != null ? new ItemEntity(
        src,
        src.etag(),
        src.objectName(),
        src.lastModified(),
        OwnerEntity.from(src.owner()),
        src.size(),
        src.storageClass(),
        src.isDeleteMarker(),
        src.isLatest(),
        src.versionId(),
        src.userMetadata(),
        src.isDir()
    ) : null;
  }

  static <T extends Item> T toItem(T item, ItemEntity src) {
    if (item != null) {
      ReflectUtils.setFieldValue(item, src.getEtag(), f -> f.getName().equals("etag"));
      ReflectUtils.setFieldValue(item, src.getObjectName(), f -> f.getName().equals("objectName"));
      ReflectUtils.setFieldValue(item, src.getLastModified() != null ? new ResponseDate(src.getLastModified()) : null, f -> f.getName().equals("lastModified"));
      ReflectUtils.setFieldValue(item, OwnerEntity.to(src.getOwner()), f -> f.getName().equals("owner"));
      ReflectUtils.setFieldValue(item, src.getSize(), f -> f.getName().equals("size"));
      ReflectUtils.setFieldValue(item, src.getStorageClass(), f -> f.getName().equals("storageClass"));
      ReflectUtils.setFieldValue(item, src.isLatest(), f -> f.getName().equals("isLatest"));
      ReflectUtils.setFieldValue(item, src.getVersionId(), f -> f.getName().equals("versionId"));
      ReflectUtils.setFieldValue(item, src.getUserMetadata(), f -> f.getName().equals("userMetadata"));
      ReflectUtils.setFieldValue(item, src.isDir(), f -> f.getName().equals("isDir"));
    }
    return item;
  }

  public static Item toDeleteMarker(ItemEntity src) {
    return toItem(new DeleteMarker(), src);
  }

  public static Item toVersion(ItemEntity src) {
    return toItem(new Version(), src);
  }

  public static Item toContents(ItemEntity src) {
    return toItem(new Contents(), src);
  }

}
