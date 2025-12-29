package com.benefitj.spring.minio;

/**
 * MinIO 模板
 */
public class MinioTemplateImpl implements IMinioTemplate {

  private MinioOptions options;

  private IMinioClient client;

  public MinioTemplateImpl() {
  }

  public MinioTemplateImpl(MinioOptions options, IMinioClient client) {
    this.options = options;
    this.client = client;
  }

  @Override
  public MinioOptions getOptions() {
    return options;
  }

  public void setOptions(MinioOptions options) {
    this.options = options;
  }

  @Override
  public IMinioClient getClient() {
    return client;
  }

  public void setClient(IMinioClient client) {
    this.client = client;
  }

}
