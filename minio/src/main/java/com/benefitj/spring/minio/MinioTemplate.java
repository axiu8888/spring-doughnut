package com.benefitj.spring.minio;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.Utils;
import io.minio.*;
import io.minio.messages.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

  private void requireBucketExist(String bucketName) {
    if (!bucketExists(bucketName)) {
      if (!getOptions().isAutoMakeBucket()) {
        throw new IllegalStateException("bucket不存在");
      }
      // 创建桶
      makeBucket(bucketName);
      getClient().removeResult();
    }
  }

  /**
   * 获取对象的信息
   *
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回检查的结果
   */
  public MinioResult<StatObjectResponse> statObject(@Nonnull String objectName, @Nonnull String bucketName) {
    return statObject(StatObjectArgs.builder(), objectName, bucketName);
  }

  /**
   * 获取对象的信息
   *
   * @param builder    Builder参数
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回检查的结果
   */
  public MinioResult<StatObjectResponse> statObject(StatObjectArgs.Builder builder,
                                                    @Nonnull String objectName,
                                                    @Nonnull String bucketName) {
    getClient().statObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取桶对象
   *
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 对象
   */
  public MinioResult<GetObjectResponse> getObject(@Nonnull String objectName,
                                                  @Nonnull String bucketName) {
    return getObject(objectName, null, null, bucketName);
  }

  /**
   * 获取桶对象
   *
   * @param objectName 对象名
   * @param start      开始的位置
   * @param length     数据长度
   * @param bucketName 桶
   * @return 是否删除
   */
  public MinioResult<GetObjectResponse> getObject(@Nonnull String objectName,
                                                  @Nullable Long start,
                                                  @Nullable Long length,
                                                  @Nonnull String bucketName) {
    return getObject(GetObjectArgs.builder().offset(start).length(length), objectName, bucketName);
  }

  /**
   * 获取桶对象
   *
   * @param builder    Builder参数
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 是否删除
   */
  public MinioResult<GetObjectResponse> getObject(GetObjectArgs.Builder builder,
                                                  @Nonnull String objectName,
                                                  @Nonnull String bucketName) {
    getClient().getObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return getClient().removeResult();
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
  public MinioResult<File> downloadObject(@Nonnull String objectName,
                                          @Nonnull String file,
                                          boolean overwrite,
                                          @Nonnull String bucketName) {
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
                                          @Nonnull String objectName,
                                          @Nonnull String file,
                                          boolean overwrite,
                                          @Nonnull String bucketName) {
    getClient().downloadObject(MinioUtils.newObjectArgs(builder.filename(file).overwrite(overwrite), objectName, bucketName));
    return getClient().removeResult().handle(r -> r.setData(r.isSuccessful() ? Paths.get(file).toFile() : null));
  }

  /**
   * 拷贝一份新的对象（服务器上会多一份新的数据）
   *
   * @param source           拷贝的源文件
   * @param destObjectName   目标对象名
   * @param taggingDirective 触发指令：拷贝或替换
   * @param bucketName       桶
   * @return 返回拷贝结果
   */
  public MinioResult<ObjectWriteResponse> copyObject(@Nonnull CopySource source,
                                                     @Nonnull String destObjectName,
                                                     @Nullable Directive taggingDirective,
                                                     @Nonnull String bucketName) {
    return copyObject(CopyObjectArgs.builder(), source, destObjectName, taggingDirective, bucketName);
  }

  /**
   * 拷贝一份新的对象（服务器上会多一份新的数据）
   *
   * @param builder          Builder参数
   * @param source           拷贝的源文件
   * @param destObjectName   目标对象名
   * @param taggingDirective 触发指令：拷贝或替换
   * @param bucketName       桶
   * @return 返回拷贝结果
   */
  public MinioResult<ObjectWriteResponse> copyObject(CopyObjectArgs.Builder builder,
                                                     @Nonnull CopySource source,
                                                     @Nonnull String destObjectName,
                                                     @Nullable Directive taggingDirective,
                                                     @Nonnull String bucketName) {
    getClient().copyObject(MinioUtils.newObjectArgs(builder
            .source(source)
            .taggingDirective(taggingDirective != null ? taggingDirective : Directive.COPY)
        , destObjectName, bucketName));
    return getClient().removeResult();
  }

  /**
   * 合并对象（将多个数据合并到一个新的数据中）
   *
   * @param objectName 对象名
   * @param sources    合并的文件源
   * @param bucketName 桶
   * @return 返回合并结果
   */
  public MinioResult<ObjectWriteResponse> composeObject(@Nonnull String objectName,
                                                        @Nonnull List<ComposeSource> sources,
                                                        @Nonnull String bucketName) {
    return composeObject(ComposeObjectArgs.builder(), objectName, sources, bucketName);
  }

  /**
   * 合并对象
   *
   * @param builder    Builder参数
   * @param objectName 对象名
   * @param sources    合并的文件源
   * @param bucketName 桶
   * @return 返回合并结果
   */
  public MinioResult<ObjectWriteResponse> composeObject(ComposeObjectArgs.Builder builder,
                                                        @Nonnull String objectName,
                                                        @Nonnull List<ComposeSource> sources,
                                                        @Nonnull String bucketName) {
    getClient().composeObject(MinioUtils.newObjectArgs(builder.sources(sources), objectName, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取对象的URL
   *
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 返回获取结果
   */
  public MinioResult<String> getPresignedObjectUrl(@Nonnull String objectName,
                                                   @Nonnull String bucketName) {
    return getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder(), objectName, bucketName);
  }

  /**
   * 获取对象的URL
   *
   * @param builder    Builder参数
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 返回获取结果
   */
  public MinioResult<String> getPresignedObjectUrl(GetPresignedObjectUrlArgs.Builder builder,
                                                   @Nonnull String objectName,
                                                   @Nonnull String bucketName) {
    getClient().getPresignedObjectUrl(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取对象的{@link PostPolicy}的表单数据，以便使用POST方法上传其数据。
   *
   * @param policy 策略
   * @return 返回获取结果
   */
  public MinioResult<Map<String, String>> getPresignedPostFormData(PostPolicy policy) {
    getClient().getPresignedPostFormData(policy);
    return getClient().removeResult();
  }

  /**
   * 移除对象
   *
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 是否移除
   */
  public boolean removeObject(@Nonnull String objectName,
                              @Nonnull String bucketName) {
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
                              @Nonnull String objectName,
                              @Nonnull String bucketName) {
    getClient().removeObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return getClient().removeResult().isSuccessful();
  }

  /**
   * 移除桶下的全部对象
   *
   * @param bucketName 桶
   * @return 返回移除的结果
   */
  public MinioResult<List<DeleteError>> removeObjects(String bucketName) {
    return removeObjects(bucketName, item -> true);
  }

  /**
   * 移除桶下的全部对象
   *
   * @param bucketName 桶
   * @param filter     过滤
   * @return 返回移除的结果
   */
  public MinioResult<List<DeleteError>> removeObjects(String bucketName, Predicate<Item> filter) {
    // 强制删除全部的数据
    MinioResult<List<Item>> r = listObjects(true, bucketName);
    if (r.isSuccessful()) {
      List<DeleteObject> list = r.getData()
          .stream()
          .filter(Objects::nonNull)
          .filter(filter)
          .map(item -> new DeleteObject(item.objectName(), item.versionId()))
          .collect(Collectors.toList());
      if (!list.isEmpty()) {
        removeObjects(RemoveObjectsArgs.builder(), list, bucketName);
        // 返回删除的结果
        return getClient().removeResult();
      }
      // 空目录，返回成功
      return MinioUtils.obtainSucceedResult();
    }
    return MinioUtils.obtainFailResult();
  }

  /**
   * 移除多个对象
   *
   * @param builder    移除的结果
   * @param bucketName 桶
   * @return 返回移除的结果
   */
  public MinioResult<List<DeleteError>> removeObjects(RemoveObjectsArgs.Builder builder,
                                                      List<DeleteObject> objects,
                                                      @Nonnull String bucketName) {
    // 删除文件
    Iterable<Result<DeleteError>> iterable =
        getClient().removeObjects(MinioUtils.newBucketArgs(builder.objects(objects), bucketName));
    return getClient().removeResult().handle(r -> {
      if (r.isSuccessful()) {
        List<DeleteError> list = new ArrayList<>();
        iterable.forEach(dr -> list.add(CatchUtils.ignore(dr::get)));
        r.setData(list);
      }
    });
  }

  /**
   * 恢复一个对象
   *
   * @param bucketName 桶
   * @return 返回合并结果
   */
  public MinioResult<Boolean> restoreObject(String bucketName) {
    return restoreObject(RestoreObjectArgs.builder(), bucketName);
  }

  /**
   * 恢复一个对象
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回合并结果
   */
  public MinioResult<Boolean> restoreObject(RestoreObjectArgs.Builder builder, String bucketName) {
    getClient().restoreObject(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult().handle(r -> r.setData(r.isSuccessful()));
  }

  /**
   * 列出全部的对象
   *
   * @param recursive  是否递归获取
   * @param bucketName 桶
   * @return 返回检查的结果
   */
  public MinioResult<List<Item>> listObjects(boolean recursive, @Nonnull String bucketName) {
    return listObjects(ListObjectsArgs.builder(), recursive, bucketName);
  }

  /**
   * 列出全部的对象
   *
   * @param builder    Builder参数
   * @param recursive  是否递归获取
   * @param bucketName 桶
   * @return 返回检查的结果
   */
  public MinioResult<List<Item>> listObjects(ListObjectsArgs.Builder builder,
                                             boolean recursive,
                                             @Nonnull String bucketName) {
    Iterable<Result<Item>> results = getClient().listObjects(MinioUtils.newBucketArgs(builder.recursive(recursive), bucketName));
    return getClient().removeResult().handle(r -> {
      List<Item> items = Utils.toList(results)
          .stream()
          .map(itemResult -> CatchUtils.tryThrow(itemResult::get, e -> {
            r.setMessage(e.getMessage());
            r.setError(e);
            return null;
          }))
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
      r.setData(items);
      if (r.getError() != null && items.isEmpty()) {
        r.setCode(400);
      }
    });
  }

  /**
   * 列出全部的桶
   */
  public MinioResult<List<Bucket>> listBuckets() {
    getClient().listBuckets();
    return getClient().removeResult();
  }

  /**
   * 列出全部的桶
   */
  public MinioResult<List<Bucket>> listBuckets(ListBucketsArgs.Builder builder) {
    getClient().listBuckets(builder.build());
    return getClient().removeResult();
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
    return Boolean.TRUE.equals(getClient().removeResult().getData());
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
    return getClient().removeResult().isSuccessful();
  }

  /**
   * 设置桶的版本
   *
   * @param status     状态
   * @param mfaDelete  是否删除
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult setBucketVersioning(VersioningConfiguration.Status status,
                                         Boolean mfaDelete,
                                         String bucketName) {
    return setBucketVersioning(SetBucketVersioningArgs.builder(), status, mfaDelete, bucketName);
  }

  /**
   * 设置桶的版本
   *
   * @param builder    Builder参数
   * @param status     状态
   * @param mfaDelete  是否删除
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult setBucketVersioning(SetBucketVersioningArgs.Builder builder,
                                         VersioningConfiguration.Status status,
                                         Boolean mfaDelete,
                                         String bucketName) {
    VersioningConfiguration config = status != null
        ? new VersioningConfiguration(status, mfaDelete)
        : new VersioningConfiguration();
    getClient().setBucketVersioning(MinioUtils.newBucketArgs(builder.config(config), bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取桶的版本
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<VersioningConfiguration> getBucketVersioning(String bucketName) {
    return getBucketVersioning(GetBucketVersioningArgs.builder(), bucketName);
  }

  /**
   * 获取桶的版本
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<VersioningConfiguration> getBucketVersioning(GetBucketVersioningArgs.Builder builder,
                                                                  String bucketName) {
    getClient().getBucketVersioning(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 在桶中设置默认对象保留
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult setObjectLockConfiguration(String bucketName, RetentionMode mode, RetentionDuration duration) {
    return setObjectLockConfiguration(SetObjectLockConfigurationArgs.builder(), mode, duration, bucketName);
  }

  /**
   * 在桶中设置默认对象保留
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult setObjectLockConfiguration(SetObjectLockConfigurationArgs.Builder builder,
                                                RetentionMode mode,
                                                RetentionDuration duration,
                                                String bucketName) {
    ObjectLockConfiguration config = new ObjectLockConfiguration(mode, duration);
    getClient().setObjectLockConfiguration(MinioUtils.newBucketArgs(builder.config(config), bucketName));
    return getClient().removeResult();
  }

  /**
   * 删除桶中的默认对象保留
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult deleteObjectLockConfiguration(String bucketName) {
    return deleteObjectLockConfiguration(DeleteObjectLockConfigurationArgs.builder(), bucketName);
  }

  /**
   * 删除桶中的默认对象保留
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult deleteObjectLockConfiguration(DeleteObjectLockConfigurationArgs.Builder builder,
                                                   String bucketName) {
    getClient().deleteObjectLockConfiguration(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取存储桶中的默认对象保留
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<ObjectLockConfiguration> getObjectLockConfiguration(String bucketName) {
    return getObjectLockConfiguration(GetObjectLockConfigurationArgs.builder(), bucketName);
  }

  /**
   * 获取存储桶中的默认对象保留
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<ObjectLockConfiguration> getObjectLockConfiguration(GetObjectLockConfigurationArgs.Builder builder,
                                                                         String bucketName) {
    getClient().getObjectLockConfiguration(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 将保留配置设置为对象
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult setObjectRetention(String bucketName) {
    return setObjectRetention(SetObjectRetentionArgs.builder(), bucketName);
  }

  /**
   * 将保留配置设置为对象
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult setObjectRetention(SetObjectRetentionArgs.Builder builder,
                                        String bucketName) {
    getClient().setObjectRetention(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取对象的保留配置
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<Retention> getObjectRetention(String bucketName) {
    return getObjectRetention(GetObjectRetentionArgs.builder(), bucketName);
  }

  /**
   * 获取对象的保留配置
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<Retention> getObjectRetention(GetObjectRetentionArgs.Builder builder,
                                                   String bucketName) {
    getClient().getObjectRetention(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 启用对对象的合法持有
   *
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult enableObjectLegalHold(String objectName, String bucketName) {
    return enableObjectLegalHold(EnableObjectLegalHoldArgs.builder().object(objectName), bucketName);
  }

  /**
   * 启用对对象的合法持有。
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult enableObjectLegalHold(EnableObjectLegalHoldArgs.Builder builder,
                                           String bucketName) {
    getClient().enableObjectLegalHold(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 禁用对象的合法持有。
   *
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult disableObjectLegalHold(String objectName, String bucketName) {
    return disableObjectLegalHold(DisableObjectLegalHoldArgs.builder().object(objectName), bucketName);
  }

  /**
   * 禁用对象的合法持有。
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult disableObjectLegalHold(DisableObjectLegalHoldArgs.Builder builder,
                                            String bucketName) {
    getClient().disableObjectLegalHold(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 如果在对象上启用了合法持有，则返回true。
   *
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<Boolean> isObjectLegalHoldEnabled(String objectName, String bucketName) {
    return isObjectLegalHoldEnabled(IsObjectLegalHoldEnabledArgs.builder().object(objectName), bucketName);
  }

  /**
   * 如果在对象上启用了合法持有，则返回true。
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<Boolean> isObjectLegalHoldEnabled(IsObjectLegalHoldEnabledArgs.Builder builder,
                                                       String bucketName) {
    getClient().isObjectLegalHoldEnabled(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 使用参数删除桶
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @param force      是否强制删除(如果是，则递归删除所有对象)
   * @return 是否删除
   */
  public boolean removeBucket(RemoveBucketArgs.Builder builder, String bucketName, boolean force) {
    if (bucketExists(bucketName)) {
      if (force) {
        removeObjects(bucketName);
      }
      getClient().removeBucket(MinioUtils.newBucketArgs(builder, bucketName));
      return getClient().removeResult().isSuccessful();
    }
    return true;
  }

  /**
   * 使用参数删除桶
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
    return removeBucket(builder, bucketName, false);
  }

  /**
   * 将数据从流上载到对象
   *
   * @param stream     数据流
   * @param objectSize 文件大小
   * @param partSize   分段大小
   * @param bucketName 桶
   * @return 上传结果
   */
  public MinioResult<ObjectWriteResponse> putObject(String objectName,
                                                    InputStream stream,
                                                    long objectSize,
                                                    long partSize,
                                                    @Nullable String bucketName) {
    partSize = Math.max(partSize, PutObjectArgs.MIN_MULTIPART_SIZE);
    return putObject(PutObjectArgs.builder().stream(stream, objectSize, partSize), objectName, bucketName);
  }

  /**
   * 将数据从流上载到对象
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 上传结果
   */
  public MinioResult<ObjectWriteResponse> putObject(PutObjectArgs.Builder builder,
                                                    @Nonnull String objectName,
                                                    @Nonnull String bucketName) {
    requireBucketExist(bucketName);
    getClient().putObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return getClient().removeResult();
  }

  /**
   * 将数据从流上载到对象
   *
   * @param builders   Builder参数
   * @param bucketName 桶
   * @return 上传结果
   */
  public MinioResult<List<ObjectWriteResponse>> putObjects(@Nonnull List<PutObjectArgs.Builder> builders,
                                                           @Nonnull String bucketName) {
    requireBucketExist(bucketName);
    List<ObjectWriteResponse> list = builders.stream()
        .map(builder -> getClient().putObject(builder
            .bucket(bucketName)
            .build()))
        .collect(Collectors.toList());
    return getClient().removeResult().handle(r -> {
      r.setData(list);
      r.setCode(200);
      r.setMessage("SUCCESS");
    });
  }

  /**
   * 上传对象
   *
   * @param objectName 对象名
   * @param file       文件路径
   * @param bucketName 桶
   * @return 是否上传
   */
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
  public MinioResult<ObjectWriteResponse> uploadObject(UploadObjectArgs.Builder builder,
                                                       @Nonnull String objectName,
                                                       @Nonnull String file,
                                                       @Nonnull String bucketName) {
    if (Files.notExists(Paths.get(file))) {
      throw new IllegalArgumentException("文件不存在: " + file + ", objectName: " + objectName);
    }
    requireBucketExist(bucketName);
    CatchUtils.tryThrow(() -> builder.filename(file));
    getClient().uploadObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取桶的桶策略配置
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<String> getBucketPolicy(String bucketName) {
    return getBucketPolicy(GetBucketPolicyArgs.builder(), bucketName);
  }

  /**
   * 获取桶的桶策略配置
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 上传结果
   */
  public MinioResult<String> getBucketPolicy(GetBucketPolicyArgs.Builder builder, @Nonnull String bucketName) {
    requireBucketExist(bucketName);
    getClient().getBucketPolicy(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 将桶策略配置设置为一个桶
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<String> setBucketPolicy(String bucketName) {
    return setBucketPolicy(SetBucketPolicyArgs.builder(), bucketName);
  }

  /**
   * 将桶策略配置设置为一个桶
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 上传结果
   */
  public MinioResult<String> setBucketPolicy(SetBucketPolicyArgs.Builder builder, @Nonnull String bucketName) {
    requireBucketExist(bucketName);
    getClient().setBucketPolicy(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 删除桶策略配置到桶
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<String> deleteBucketPolicy(String bucketName) {
    return deleteBucketPolicy(DeleteBucketPolicyArgs.builder(), bucketName);
  }

  /**
   * 删除桶策略配置到桶
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 上传结果
   */
  public MinioResult<String> deleteBucketPolicy(DeleteBucketPolicyArgs.Builder builder, @Nonnull String bucketName) {
    requireBucketExist(bucketName);
    getClient().deleteBucketPolicy(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 设置桶的生命周期配置
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> setBucketLifecycle(List<LifecycleRule> rules, String bucketName) {
    return setBucketLifecycle(SetBucketLifecycleArgs.builder().config(new LifecycleConfiguration(rules)), bucketName);
  }

  /**
   * 设置桶的生命周期配置
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> setBucketLifecycle(SetBucketLifecycleArgs.Builder builder,
                                                                @Nonnull String bucketName) {
    getClient().setBucketLifecycle(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取桶的生命周期配置
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> deleteBucketLifecycle(String bucketName) {
    return deleteBucketLifecycle(DeleteBucketLifecycleArgs.builder(), bucketName);
  }

  /**
   * 获取桶的生命周期配置
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> deleteBucketLifecycle(DeleteBucketLifecycleArgs.Builder builder,
                                                                   @Nonnull String bucketName) {
    getClient().deleteBucketLifecycle(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取桶的生命周期配置
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> getBucketLifecycle(String bucketName) {
    return getBucketLifecycle(GetBucketLifecycleArgs.builder(), bucketName);
  }

  /**
   * 获取桶的生命周期配置
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> getBucketLifecycle(GetBucketLifecycleArgs.Builder builder,
                                                                @Nonnull String bucketName) {
    getClient().getBucketLifecycle(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取存储桶的通知配置
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> getBucketNotification(String bucketName) {
    return getBucketNotification(GetBucketNotificationArgs.builder(), bucketName);
  }

  /**
   * 获取存储桶的通知配置
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> getBucketNotification(GetBucketNotificationArgs.Builder builder,
                                                                   @Nonnull String bucketName) {
    getClient().getBucketNotification(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 将通知配置设置为一个桶
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> setBucketNotification(List<CloudFunctionConfiguration> cloudFunctionConfigurationList,
                                                                   List<QueueConfiguration> queueConfigurationList,
                                                                   List<TopicConfiguration> topicConfigurationList,
                                                                   String bucketName) {
    return setBucketNotification(SetBucketNotificationArgs.builder(), cloudFunctionConfigurationList, queueConfigurationList, topicConfigurationList, bucketName);
  }

  /**
   * 将通知配置设置为一个桶
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> setBucketNotification(SetBucketNotificationArgs.Builder builder,
                                                                   List<CloudFunctionConfiguration> cloudFunctionConfigurationList,
                                                                   List<QueueConfiguration> queueConfigurationList,
                                                                   List<TopicConfiguration> topicConfigurationList,
                                                                   @Nonnull String bucketName) {
    NotificationConfiguration config = new NotificationConfiguration();
    config.setCloudFunctionConfigurationList(cloudFunctionConfigurationList);
    config.setQueueConfigurationList(queueConfigurationList);
    config.setTopicConfigurationList(topicConfigurationList);
    getClient().setBucketNotification(MinioUtils.newBucketArgs(builder.config(config), bucketName));
    return getClient().removeResult();
  }

  /**
   * 删除桶的通知配置
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> deleteBucketNotification(String bucketName) {
    return deleteBucketNotification(DeleteBucketNotificationArgs.builder(), bucketName);
  }

  /**
   * 删除桶的通知配置
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<LifecycleConfiguration> deleteBucketNotification(DeleteBucketNotificationArgs.Builder builder,
                                                                      @Nonnull String bucketName) {
    getClient().deleteBucketNotification(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取桶的桶复制配置
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<ReplicationConfiguration> getBucketReplication(String bucketName) {
    getClient().getBucketReplication(MinioUtils.newBucketArgs(GetBucketReplicationArgs.builder(), bucketName));
    return getClient().removeResult();
  }

  /**
   * 获取桶的桶复制配置
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<ReplicationConfiguration> getBucketReplication(GetBucketReplicationArgs.Builder builder,
                                                                    @Nonnull String bucketName) {
    getClient().getBucketReplication(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 将桶复制配置设置为一个桶
   *
   * @param role       角色
   * @param rules      规则
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult setBucketReplication(@Nullable String role,
                                          @Nonnull List<ReplicationRule> rules,
                                          @Nonnull String bucketName) {
    return setBucketReplication(SetBucketReplicationArgs.builder(), role, rules, bucketName);
  }

  /**
   * 将桶复制配置设置为一个桶
   *
   * @param builder    Builder参数
   * @param role       角色
   * @param rules      规则
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult setBucketReplication(SetBucketReplicationArgs.Builder builder,
                                          @Nullable String role,
                                          @Nonnull List<ReplicationRule> rules,
                                          @Nonnull String bucketName) {
    ReplicationConfiguration config = new ReplicationConfiguration(role, rules);
    getClient().setBucketReplication(MinioUtils.newBucketArgs(builder.config(config), bucketName));
    return getClient().removeResult();
  }

  /**
   * 设置桶的副本数量
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult deleteBucketReplication(@Nonnull String bucketName) {
    return deleteBucketReplication(DeleteBucketReplicationArgs.builder(), bucketName);
  }

  /**
   * 设置桶的副本数量
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult deleteBucketReplication(DeleteBucketReplicationArgs.Builder builder,
                                             @Nonnull String bucketName) {
    getClient().deleteBucketReplication(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 监听桶的对象前缀和后缀的事件。返回的可闭迭代器为惰性评估，因此需要迭代以获得新记录，并且必须与try-with-resource一起使用以释放底层网络资源
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<CloseableIterator<Result<NotificationRecords>>> listenBucketNotification(@Nonnull String bucketName) {
    return listenBucketNotification(ListenBucketNotificationArgs.builder(), bucketName);
  }

  /**
   * 监听桶的对象前缀和后缀的事件。返回的可闭迭代器为惰性评估，因此需要迭代以获得新记录，并且必须与try-with-resource一起使用以释放底层网络资源
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<CloseableIterator<Result<NotificationRecords>>> listenBucketNotification(ListenBucketNotificationArgs.Builder builder,
                                                                                              @Nonnull String bucketName) {
    getClient().listenBucketNotification(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 根据SQL表达式选择对象的内容
   *
   * @param sqlExpression SQL表达式
   * @param bucketName    桶
   * @return 返回结果
   */
  public MinioResult<SelectResponseStream> selectObjectContent(String sqlExpression, @Nonnull String bucketName) {
    return selectObjectContent(SelectObjectContentArgs.builder().sqlExpression(sqlExpression), bucketName);
  }

  /**
   * 根据SQL表达式选择对象的内容
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<SelectResponseStream> selectObjectContent(SelectObjectContentArgs.Builder builder,
                                                               @Nonnull String bucketName) {
    getClient().selectObjectContent(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  // setBucketEncryption ...
  // getBucketEncryption ...
  // deleteBucketEncryption ...

  /**
   * 获取桶的标签
   *
   * @param bucketName 桶
   * @return 返回调用结果
   */
  public MinioResult<Map<String, String>> getBucketTags(String bucketName) {
    return getBucketTags(GetBucketTagsArgs.builder(), bucketName);
  }

  /**
   * 获取桶的标签
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回调用结果
   */
  public MinioResult<Map<String, String>> getBucketTags(GetBucketTagsArgs.Builder builder, String bucketName) {
    Tags bucketTags = getClient().getBucketTags(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult().handle(r -> r.setData(bucketTags != null ? bucketTags.get() : null));
  }

  /**
   * 设置桶的标签
   *
   * @param tags       标签
   * @param bucketName 桶
   * @return 返回是否设置成功
   */
  public MinioResult<Boolean> setBucketTags(Map<String, String> tags, String bucketName) {
    return setBucketTags(SetBucketTagsArgs.builder(), tags, bucketName);
  }

  /**
   * 设置桶的标签
   *
   * @param builder    Builder参数
   * @param tags       标签
   * @param bucketName 桶
   * @return 返回是否设置成功
   */
  public MinioResult<Boolean> setBucketTags(SetBucketTagsArgs.Builder builder,
                                            Map<String, String> tags,
                                            String bucketName) {
    getClient().setBucketTags(MinioUtils.newBucketArgs(builder.tags(tags), bucketName));
    return getClient().removeResult().handle(r -> r.setData(r.isSuccessful()));
  }

  /**
   * 刪除桶的标签
   *
   * @param bucketName 桶
   * @return 返回合并结果
   */
  public MinioResult<Boolean> deleteBucketTags(String bucketName) {
    return deleteBucketTags(DeleteBucketTagsArgs.builder(), bucketName);
  }

  /**
   * 刪除桶的标签
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回合并结果
   */
  public MinioResult<Boolean> deleteBucketTags(DeleteBucketTagsArgs.Builder builder, String bucketName) {
    getClient().deleteBucketTags(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult().handle(r -> r.setData(r.isSuccessful()));
  }

  /**
   * 获取对象的标签
   *
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 返回获取结果
   */
  public MinioResult<Tags> getObjectTags(@Nonnull String objectName,
                                         @Nonnull String bucketName) {
    return getObjectTags(GetObjectTagsArgs.builder(), objectName, bucketName);
  }

  /**
   * 获取对象的标签
   *
   * @param builder    Builder参数
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 返回获取结果
   */
  public MinioResult<Tags> getObjectTags(GetObjectTagsArgs.Builder builder,
                                         @Nonnull String objectName,
                                         @Nonnull String bucketName) {
    getClient().getObjectTags(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return getClient().removeResult();
  }

  /**
   * 设置对象的标签
   *
   * @param objectName 对象名
   * @param tags       标签
   * @param bucketName 桶
   * @return 返回是否设置成功
   */
  public MinioResult<Boolean> setObjectTags(@Nonnull String objectName,
                                            Map<String, String> tags,
                                            @Nonnull String bucketName) {
    return setObjectTags(SetObjectTagsArgs.builder(), objectName, tags, bucketName);
  }

  /**
   * 设置对象的标签
   *
   * @param builder    Builder参数
   * @param objectName 对象名
   * @param tags       标签
   * @param bucketName 桶
   * @return 返回是否设置成功
   */
  public MinioResult<Boolean> setObjectTags(SetObjectTagsArgs.Builder builder,
                                            @Nonnull String objectName,
                                            Map<String, String> tags,
                                            @Nonnull String bucketName) {
    getClient().setObjectTags(MinioUtils.newObjectArgs(builder.tags(tags), objectName, bucketName));
    return getClient().removeResult().handle(r -> r.setData(r.isSuccessful()));
  }

  /**
   * 删除对象的标记
   *
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<Boolean> deleteObjectTags(@Nonnull String objectName,
                                               @Nonnull String bucketName) {
    return deleteObjectTags(DeleteObjectTagsArgs.builder(), objectName, bucketName);
  }

  /**
   * 删除对象的标记
   *
   * @param builder    Builder参数
   * @param objectName 对象名
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<Boolean> deleteObjectTags(DeleteObjectTagsArgs.Builder builder,
                                               @Nonnull String objectName,
                                               @Nonnull String bucketName) {
    getClient().deleteObjectTags(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return getClient().removeResult().handle(r -> r.setData(r.isSuccessful()));
  }

  /**
   * 上传对象，有点问题，上传文件较多时会报内存溢出的错误
   *
   * @param objects         对象
   * @param compression     是否压缩
   * @param stagingFilename 临时文件名
   * @param bucketName      桶
   * @return 返回结果
   */
  @Deprecated
  public MinioResult<ObjectWriteResponse> uploadSnowballObjects(List<SnowballObject> objects,
                                                                boolean compression,
                                                                @Nullable String stagingFilename,
                                                                @Nonnull String bucketName) {
    return uploadSnowballObjects(UploadSnowballObjectsArgs.builder()
            .objects(objects)
            .compression(compression)
            .stagingFilename(stagingFilename)
        , bucketName);
  }

  /**
   * 上传对象，有点问题，上传文件较多时会报内存溢出的错误
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 上传结果
   */
  @Deprecated
  public MinioResult<ObjectWriteResponse> uploadSnowballObjects(UploadSnowballObjectsArgs.Builder builder,
                                                                @Nonnull String bucketName) {
    requireBucketExist(bucketName);
    getClient().uploadSnowballObjects(MinioUtils.newBucketArgs(builder, bucketName));
    return getClient().removeResult();
  }

  /**
   * 添加桶的标签
   *
   * @param tags       标签
   * @param bucketName 桶
   * @return 返回是否设置成功
   */
  public MinioResult<Boolean> addBucketTags(Map<String, String> tags, String bucketName) {
    return addBucketTags(SetBucketTagsArgs.builder(), tags, bucketName);
  }

  /**
   * 添加桶的标签
   *
   * @param builder    Builder参数
   * @param tags       标签
   * @param bucketName 桶
   * @return 返回是否设置成功
   */
  public MinioResult<Boolean> addBucketTags(SetBucketTagsArgs.Builder builder,
                                            Map<String, String> tags,
                                            String bucketName) {
    MinioResult<Map<String, String>> result = getBucketTags(bucketName);
    if (!result.isSuccessful()) {
      return MinioResult.fail(result.getMessage());
    }
    Map<String, String> newTags = new LinkedHashMap<>();
    newTags.putAll(result.getData());
    newTags.putAll(tags);
    return setBucketTags(builder, newTags, bucketName);
  }

}
