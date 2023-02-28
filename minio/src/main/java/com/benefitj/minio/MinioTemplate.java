package com.benefitj.minio;

import com.benefitj.core.CatchUtils;
import io.minio.*;
import io.minio.messages.Bucket;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MinioTemplate {

  private MinioOptions options;

  private IMinioClient client;

  public MinioTemplate() {
  }

  public MinioTemplate(MinioOptions options, IMinioClient client) {
    this.options = options;
    this.client = client;
  }

  public MinioOptions getOptions() {
    return options;
  }

  public void setOptions(MinioOptions options) {
    this.options = options;
  }

  public IMinioClient getClient() {
    return client;
  }

  public void setClient(IMinioClient client) {
    this.client = client;
  }

  public <T> T execute(Function<IMinioClient, T> mappedFunc) {
    return mappedFunc.apply(getClient());
  }

  /**
   * 是否存在桶
   *
   * @param bucketName 桶
   * @return 返回检查的结果
   */
  public boolean bucketExists(String bucketName) {
    return bucketExists(bucketName, null, null, null);
  }

  /**
   * 是否存在桶
   *
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 返回检查的结果
   */
  public boolean bucketExists(String bucketName,
                              @Nullable String region,
                              @Nullable Map<String, String> extraHeaders,
                              @Nullable Map<String, String> extraQueryParams) {
    getClient().bucketExists(MinioUtils.newBucketArgs(BucketExistsArgs.builder(), bucketName, region, extraHeaders, extraQueryParams));
    return Boolean.TRUE.equals(MinioUtils.removeResult().getData());
  }

  /**
   * 列出全部的桶
   */
  public MinioResult<List<Bucket>> listBuckets() {
    getClient().listBuckets();
    return MinioUtils.removeResult();
  }

  /**
   * 创建桶，如果已存在，返回true，否则就创建新的
   *
   * @param bucketName 桶
   */
  public boolean makeBucket(String bucketName) {
    return makeBucket(bucketName, null, null, null);
  }

  /**
   * 创建桶，如果已存在，返回true，否则就创建新的
   *
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 是否创建
   */
  public boolean makeBucket(String bucketName,
                            @Nullable String region,
                            @Nullable Map<String, String> extraHeaders,
                            @Nullable Map<String, String> extraQueryParams) {
    if (bucketExists(bucketName, region, null, null)) {
      return true;
    }
    getClient().makeBucket(MinioUtils.newBucketArgs(MakeBucketArgs.builder(), bucketName, region, extraHeaders, extraQueryParams));
    return MinioUtils.removeResult().isSuccessful();
  }

  /**
   * 删除桶，如果不存在，返回true，否则就删除
   *
   * @param bucketName 桶
   */
  public boolean removeBucket(String bucketName) {
    return removeBucket(bucketName, null, null, null);
  }

  /**
   * 删除桶
   *
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 是否删除
   */
  public boolean removeBucket(String bucketName,
                              @Nullable String region,
                              @Nullable Map<String, String> extraHeaders,
                              @Nullable Map<String, String> extraQueryParams) {
    if (bucketExists(bucketName, region, null, null)) {
      getClient().removeBucket(MinioUtils.newBucketArgs(RemoveBucketArgs.builder(), bucketName, region, extraHeaders, extraQueryParams));
      return MinioUtils.removeResult().isSuccessful();
    }
    return true;
  }

  /**
   * 是否存在对象
   *
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回检查的结果
   */
  @Nullable
  public MinioResult<StatObjectResponse> statObject(String objectName,
                                                    String bucketName) {
    return statObject(objectName, bucketName, null, null, null);
  }

  /**
   * 是否存在对象
   *
   * @param objectName       对象
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 返回检查的结果
   */
  public MinioResult<StatObjectResponse> statObject(String objectName,
                                                    String bucketName,
                                                    @Nullable String region,
                                                    @Nullable Map<String, String> extraHeaders,
                                                    @Nullable Map<String, String> extraQueryParams) {
    getClient().statObject(MinioUtils.newObjectArgs(StatObjectArgs.builder(), objectName, bucketName, region, extraHeaders, extraQueryParams));
    return MinioUtils.removeResult();
  }

  /**
   * 上传对象
   *
   * @param objectName 对象名
   * @param file       文件路径
   * @param bucketName 桶
   * @return 是否上传
   */
  @Nullable
  public MinioResult<ObjectWriteResponse> uploadObject(@Nonnull String objectName,
                                                       @Nonnull String file,
                                                       @Nonnull String bucketName) {
    return uploadObject(objectName, file, bucketName, null, null, null);
  }

  /**
   * 上传对象
   *
   * @param objectName       对象名
   * @param file             文件路径
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 是否上传
   */
  @Nullable
  public MinioResult<ObjectWriteResponse> uploadObject(@Nonnull String objectName,
                                                       @Nonnull String file,
                                                       @Nonnull String bucketName,
                                                       @Nullable String region,
                                                       @Nullable Map<String, String> extraHeaders,
                                                       @Nullable Map<String, String> extraQueryParams) {
    if (Files.notExists(Paths.get(file))) {
      throw new IllegalArgumentException("文件不存在: " + file + ", objectName: " + objectName);
    }
    if (!bucketExists(bucketName, region, null, null)) {
      if (!getOptions().isAutoMakeBucket()) {
        return MinioResult.fail("bucket不存在，无法保存数据!");
      }
      // 创建桶
      makeBucket(bucketName);
      MinioUtils.removeResult();
    }
    getClient().uploadObject(MinioUtils.newObjectArgs(
        CatchUtils.tryThrow(() -> UploadObjectArgs.builder().filename(file)), objectName, bucketName, region, extraHeaders, extraQueryParams));
    return MinioUtils.removeResult();
  }

  /**
   * 获取桶对象
   *
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 对象
   */
  @Nullable
  public MinioResult<GetObjectResponse> getObject(GetObjectArgs.Builder builder, String objectName, String bucketName) {
    getClient().getObject(MinioUtils.newObjectArgs(builder, objectName, bucketName, null, null, null));
    return MinioUtils.removeResult();
  }

  /**
   * 获取桶对象
   *
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 对象
   */
  @Nullable
  public MinioResult<GetObjectResponse> getObject(String objectName, String bucketName) {
    return getObject(objectName, bucketName, null, null, null);
  }

  /**
   * 获取桶对象
   *
   * @param objectName       对象名
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 是否删除
   */
  @Nullable
  public MinioResult<GetObjectResponse> getObject(String objectName,
                                                  String bucketName,
                                                  @Nullable String region,
                                                  @Nullable Map<String, String> extraHeaders,
                                                  @Nullable Map<String, String> extraQueryParams) {
    getClient().getObject(MinioUtils.newObjectArgs(
        GetObjectArgs.builder(), objectName, bucketName, region, extraHeaders, extraQueryParams));
    return MinioUtils.removeResult();
  }

  /**
   * 下载对象
   *
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 是否下载
   */
  public MinioResult<File> downloadObject(DownloadObjectArgs.Builder builder,
                                          String objectName,
                                          String bucketName) {
    DownloadObjectArgs args = MinioUtils.newObjectArgs(builder, objectName, bucketName, null, null, null);
    if (StringUtils.isBlank(args.filename())) {
      throw new IllegalArgumentException("目标文件路径不能为空!");
    }
    getClient().downloadObject(args);
    return MinioUtils.removeResult().handle(r -> r.setData(r.isSuccessful() ? Paths.get(args.filename()).toFile() : null));
  }

  /**
   * 下载对象
   *
   * @param objectName 对象名
   * @param file       文件路径
   * @param overwrite  是否覆盖已存在的文件
   * @param bucketName 桶
   * @return 是否下载
   */
  public MinioResult<File> downloadObject(String objectName,
                                          String file,
                                          boolean overwrite,
                                          String bucketName) {
    return downloadObject(objectName, file, overwrite, bucketName, null, null, null);
  }

  /**
   * 下载对象
   *
   * @param objectName       对象名
   * @param file             文件路径
   * @param overwrite        是否覆盖已存在的文件
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 是否下载
   */
  public MinioResult<File> downloadObject(String objectName,
                                          String file,
                                          boolean overwrite,
                                          String bucketName,
                                          @Nullable String region,
                                          @Nullable Map<String, String> extraHeaders,
                                          @Nullable Map<String, String> extraQueryParams) {
    getClient().downloadObject(MinioUtils.newObjectArgs(DownloadObjectArgs.builder().filename(file).overwrite(overwrite), objectName, bucketName, region, extraHeaders, extraQueryParams));
    return MinioUtils.removeResult().handle(r -> r.setData(r.isSuccessful() ? Paths.get(file).toFile() : null));
  }

  /**
   * 移除对象
   *
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 是否移除
   */
  public boolean removeObject(String objectName, String bucketName) {
    return removeObject(objectName, bucketName, null, null, null);
  }

  /**
   * 移除对象
   *
   * @param objectName       对象名
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 是否移除
   */
  public boolean removeObject(String objectName,
                              String bucketName,
                              @Nullable String region,
                              @Nullable Map<String, String> extraHeaders,
                              @Nullable Map<String, String> extraQueryParams) {
    getClient().removeObject(MinioUtils.newObjectArgs(RemoveObjectArgs.builder(), objectName, bucketName, region, extraHeaders, extraQueryParams));
    return MinioUtils.removeResult().isSuccessful();
  }

  /**
   * 拷贝一份新的对象
   *
   * @param objectName       对象名
   * @param source           拷贝的源文件
   * @param taggingDirective 触发指令：拷贝或替换
   * @param bucketName       桶
   * @return 是否下载
   */
  public MinioResult<ObjectWriteResponse> copyObject(String objectName,
                                                     @Nonnull CopySource source,
                                                     @Nullable Directive taggingDirective,
                                                     @Nonnull String bucketName) {
    return copyObject(objectName, source, taggingDirective, null, bucketName, null, null, null);
  }

  /**
   * 拷贝一份新的对象
   *
   * @param objectName       对象名
   * @param source           拷贝的源文件
   * @param taggingDirective 触发指令：拷贝或替换
   * @param userMetadata     用户元数据
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 是否下载
   */
  public MinioResult<ObjectWriteResponse> copyObject(String objectName,
                                                     @Nonnull CopySource source,
                                                     @Nullable Directive taggingDirective,
                                                     @Nullable Map<String, String> userMetadata,
                                                     @Nonnull String bucketName,
                                                     @Nullable String region,
                                                     @Nullable Map<String, String> extraHeaders,
                                                     @Nullable Map<String, String> extraQueryParams) {
    getClient().copyObject(MinioUtils.newObjectArgs(CopyObjectArgs.builder()
            .source(source)
            .taggingDirective(taggingDirective != null ? taggingDirective : Directive.COPY)
            .userMetadata(userMetadata)
        , objectName, bucketName, region, extraHeaders, extraQueryParams));
    return MinioUtils.removeResult();
  }

  /**
   * 合并对象
   *
   * @param objectName   对象名
   * @param sources      合并的文件源
   * @param userMetadata 用户元数据
   * @param bucketName   桶
   * @return 是否下载
   */
  public MinioResult<ObjectWriteResponse> composeObject(String objectName,
                                                        @Nonnull List<ComposeSource> sources,
                                                        @Nullable Map<String, String> userMetadata,
                                                        @Nonnull String bucketName) {
    return composeObject(objectName, sources, userMetadata, bucketName, null, null, null);
  }

  /**
   * 合并对象
   *
   * @param objectName       对象名
   * @param sources          合并的文件源
   * @param userMetadata     用户元数据
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 是否下载
   */
  public MinioResult<ObjectWriteResponse> composeObject(String objectName,
                                                        @Nonnull List<ComposeSource> sources,
                                                        @Nullable Map<String, String> userMetadata,
                                                        @Nonnull String bucketName,
                                                        @Nullable String region,
                                                        @Nullable Map<String, String> extraHeaders,
                                                        @Nullable Map<String, String> extraQueryParams) {
    getClient().composeObject(MinioUtils.newObjectArgs(ComposeObjectArgs.builder()
            .sources(sources)
            .userMetadata(userMetadata)
        , objectName, bucketName, region, extraHeaders, extraQueryParams));
    return MinioUtils.removeResult();
  }


}
