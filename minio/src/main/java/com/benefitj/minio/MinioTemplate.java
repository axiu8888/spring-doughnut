package com.benefitj.minio;

import com.benefitj.core.CatchUtils;
import io.minio.*;
import io.minio.messages.Bucket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * MinIO 模板
 */
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
    return bucketExists(BucketExistsArgs.builder(), bucketName);
  }

  /**
   * 是否存在桶
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回检查的结果
   */
  public boolean bucketExists(BucketExistsArgs.Builder builder, String bucketName) {
    getClient().bucketExists(MinioUtils.newBucketArgs(builder, bucketName));
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
    return makeBucket(MakeBucketArgs.builder(), bucketName);
  }

  /**
   * 创建桶，如果已存在，返回true，否则就创建新的
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 是否创建
   */
  public boolean makeBucket(MakeBucketArgs.Builder builder, String bucketName) {
    if (bucketExists(bucketName)) {
      return true;
    }
    getClient().makeBucket(MinioUtils.newBucketArgs(builder, bucketName));
    return MinioUtils.removeResult().isSuccessful();
  }

  /**
   * 删除桶，如果不存在，返回true，否则就删除
   *
   * @param bucketName 桶
   */
  public boolean removeBucket(String bucketName) {
    return removeBucket(RemoveBucketArgs.builder(), bucketName);
  }

  /**
   * 删除桶
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 是否删除
   */
  public boolean removeBucket(RemoveBucketArgs.Builder builder, String bucketName) {
    if (bucketExists(bucketName)) {
      getClient().removeBucket(MinioUtils.newBucketArgs(builder, bucketName));
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
  public MinioResult<StatObjectResponse> statObject(String objectName, String bucketName) {
    return statObject(StatObjectArgs.builder(), objectName, bucketName);
  }

  /**
   * 是否存在对象
   *
   * @param builder    Builder参数
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回检查的结果
   */
  public MinioResult<StatObjectResponse> statObject(StatObjectArgs.Builder builder,
                                                    String objectName,
                                                    String bucketName) {
    getClient().statObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
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
    return uploadObject(UploadObjectArgs.builder(), objectName, file, bucketName);
  }

  /**
   * 上传对象
   *
   * @param builder    Builder参数
   * @param objectName 对象名
   * @param file       文件路径
   * @param bucketName 桶
   * @return 是否上传
   */
  @Nullable
  public MinioResult<ObjectWriteResponse> uploadObject(UploadObjectArgs.Builder builder,
                                                       @Nonnull String objectName,
                                                       @Nonnull String file,
                                                       @Nonnull String bucketName) {
    if (Files.notExists(Paths.get(file))) {
      throw new IllegalArgumentException("文件不存在: " + file + ", objectName: " + objectName);
    }
    if (!bucketExists(bucketName)) {
      if (!getOptions().isAutoMakeBucket()) {
        return MinioResult.fail("bucket不存在，无法保存数据!");
      }
      // 创建桶
      makeBucket(bucketName);
      MinioUtils.removeResult();
    }
    CatchUtils.tryThrow(() -> builder.filename(file));
    getClient().uploadObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
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
    return getObject(GetObjectArgs.builder(), objectName, bucketName);
  }

  /**
   * 获取桶对象
   *
   * @param builder    Builder参数
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 是否删除
   */
  @Nullable
  public MinioResult<GetObjectResponse> getObject(GetObjectArgs.Builder builder,
                                                  String objectName,
                                                  String bucketName) {
    getClient().getObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return MinioUtils.removeResult();
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
    return downloadObject(DownloadObjectArgs.builder(), objectName, file, overwrite, bucketName);
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
  public MinioResult<File> downloadObject(DownloadObjectArgs.Builder builder,
                                          String objectName,
                                          String file,
                                          boolean overwrite,
                                          String bucketName) {
    getClient().downloadObject(MinioUtils.newObjectArgs(builder.filename(file).overwrite(overwrite), objectName, bucketName));
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
    return removeObject(RemoveObjectArgs.builder(), objectName, bucketName);
  }

  /**
   * 移除对象
   *
   * @param builder    Builder参数
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 是否移除
   */
  public boolean removeObject(RemoveObjectArgs.Builder builder,
                              String objectName,
                              String bucketName) {
    getClient().removeObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return MinioUtils.removeResult().isSuccessful();
  }

  /**
   * 拷贝一份新的对象（服务器上会多一份新的数据）
   *
   * @param objectName       对象名
   * @param source           拷贝的源文件
   * @param taggingDirective 触发指令：拷贝或替换
   * @param bucketName       桶
   * @return 返回拷贝结果
   */
  public MinioResult<ObjectWriteResponse> copyObject(String objectName,
                                                     @Nonnull CopySource source,
                                                     @Nullable Directive taggingDirective,
                                                     @Nonnull String bucketName) {
    return copyObject(CopyObjectArgs.builder(), objectName, source, taggingDirective, null, bucketName);
  }

  /**
   * 拷贝一份新的对象（服务器上会多一份新的数据）
   *
   * @param builder          Builder参数
   * @param objectName       对象名
   * @param source           拷贝的源文件
   * @param taggingDirective 触发指令：拷贝或替换
   * @param userMetadata     用户元数据
   * @param bucketName       桶
   * @return 返回拷贝结果
   */
  public MinioResult<ObjectWriteResponse> copyObject(CopyObjectArgs.Builder builder,
                                                     String objectName,
                                                     @Nonnull CopySource source,
                                                     @Nullable Directive taggingDirective,
                                                     @Nullable Map<String, String> userMetadata,
                                                     @Nonnull String bucketName) {
    getClient().copyObject(MinioUtils.newObjectArgs(builder
            .source(source)
            .taggingDirective(taggingDirective != null ? taggingDirective : Directive.COPY)
            .userMetadata(userMetadata)
        , objectName, bucketName));
    return MinioUtils.removeResult();
  }

  /**
   * 合并对象（将多个数据合并到一个新的数据中）
   *
   * @param objectName   对象名
   * @param sources      合并的文件源
   * @param userMetadata 用户元数据
   * @param bucketName   桶
   * @return 返回合并结果
   */
  public MinioResult<ObjectWriteResponse> composeObject(String objectName,
                                                        @Nonnull List<ComposeSource> sources,
                                                        @Nullable Map<String, String> userMetadata,
                                                        @Nonnull String bucketName) {
    return composeObject(ComposeObjectArgs.builder(), objectName, sources, userMetadata, bucketName);
  }

  /**
   * 合并对象
   *
   * @param builder      Builder参数
   * @param objectName   对象名
   * @param sources      合并的文件源
   * @param userMetadata 用户元数据
   * @param bucketName   桶
   * @return 返回合并结果
   */
  public MinioResult<ObjectWriteResponse> composeObject(ComposeObjectArgs.Builder builder,
                                                        String objectName,
                                                        @Nonnull List<ComposeSource> sources,
                                                        @Nullable Map<String, String> userMetadata,
                                                        @Nonnull String bucketName) {
    getClient().composeObject(MinioUtils.newObjectArgs(builder
            .sources(sources)
            .userMetadata(userMetadata)
        , objectName, bucketName));
    return MinioUtils.removeResult();
  }


}
