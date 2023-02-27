package com.benefitj.minio;

import io.minio.BucketExistsArgs;
import io.minio.messages.Bucket;

import java.util.List;

public class MinIOTemplate {

  private MinIOOptions options;

  private IMinIOClient client;

  private MinIOHelper minIOHelper = MinIOHelper.get();

  public MinIOTemplate() {
  }

  public MinIOTemplate(MinIOOptions options, IMinIOClient client) {
    this.options = options;
    this.client = client;
  }

  public MinIOOptions getOptions() {
    return options;
  }

  public void setOptions(MinIOOptions options) {
    this.options = options;
  }

  public IMinIOClient getClient() {
    return client;
  }

  public void setClient(IMinIOClient client) {
    this.client = client;
  }

  public boolean isExistBucket(BucketExistsArgs args) {
    return getClient().bucketExists(args);
  }

  public List<Bucket> listBuckets() {
    return getClient().listBuckets();
  }

}
