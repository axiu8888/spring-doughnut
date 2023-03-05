package com.hsrg.minio;

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
      MinioUtils.removeResult();
    }
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
    return MinioUtils.removeResult().handle(r -> r.setData(r.isSuccessful()));
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
    return MinioUtils.removeResult().handle(r -> r.setData(bucketTags != null ? bucketTags.get() : null));
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
    return MinioUtils.removeResult().handle(r -> r.setData(r.isSuccessful()));
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
    return MinioUtils.removeResult();
  }

  /**
   * 设置桶的版本
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<VersioningConfiguration> getBucketVersioning(String bucketName) {
    return getBucketVersioning(GetBucketVersioningArgs.builder(), bucketName);
  }

  /**
   * 设置桶的版本
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<VersioningConfiguration> getBucketVersioning(GetBucketVersioningArgs.Builder builder,
                                                                  String bucketName) {
    getClient().getBucketVersioning(MinioUtils.newBucketArgs(builder, bucketName));
    return MinioUtils.removeResult();
  }

  /**
   * 设置桶的副本数量
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
   * 设置桶的副本数量
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
    return MinioUtils.removeResult();
  }

  /**
   * 获取桶的副本规则
   *
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<ReplicationConfiguration> getBucketReplication(String bucketName) {
    getClient().getBucketReplication(MinioUtils.newBucketArgs(GetBucketReplicationArgs.builder(), bucketName));
    return MinioUtils.removeResult();
  }

  /**
   * 获取桶的副本规则
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @return 返回结果
   */
  public MinioResult<ReplicationConfiguration> getBucketReplication(GetBucketReplicationArgs.Builder builder,
                                                                    @Nonnull String bucketName) {
    getClient().getBucketReplication(MinioUtils.newBucketArgs(builder, bucketName));
    return MinioUtils.removeResult();
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
    return MinioUtils.removeResult();
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
    return removeBucket(builder, bucketName, false);
  }

  /**
   * 删除桶
   *
   * @param builder    Builder参数
   * @param bucketName 桶
   * @param force      是否强制删除
   * @return 是否删除
   */
  public boolean removeBucket(RemoveBucketArgs.Builder builder, String bucketName, boolean force) {
    if (bucketExists(bucketName)) {
      if (force) {
        removeObjects(bucketName);
      }
      getClient().removeBucket(MinioUtils.newBucketArgs(builder, bucketName));
      return MinioUtils.removeResult().isSuccessful();
    }
    return true;
  }

  /**
   * 移除桶下的全部对象
   *
   * @param bucketName 桶
   * @return 返回移除的结果
   */
  public MinioResult<List<DeleteError>> removeObjects(String bucketName) {
    // 强制删除全部的数据
    MinioResult<List<Item>> r = listObjects(true, bucketName);
    if (r.isSuccessful()) {
      List<DeleteObject> list = r.getData()
          .stream()
          .filter(Objects::nonNull)
          .map(item -> new DeleteObject(item.objectName(), item.versionId()))
          .collect(Collectors.toList());
      if (!list.isEmpty()) {
        removeObjects(RemoveObjectsArgs.builder(), list, bucketName);
        // 返回删除的结果
        return MinioUtils.removeResult();
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
    return MinioUtils.removeResult().handle(r -> {
      if (r.isSuccessful()) {
        List<DeleteError> list = new ArrayList<>();
        iterable.forEach(dr -> list.add(CatchUtils.ignore(dr::get)));
        r.setData(list);
      }
    });
  }

  /**
   * 是否存在对象
   *
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回检查的结果
   */
  public MinioResult<StatObjectResponse> statObject(@Nonnull String objectName, @Nonnull String bucketName) {
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
                                                    @Nonnull String objectName,
                                                    @Nonnull String bucketName) {
    getClient().statObject(MinioUtils.newObjectArgs(builder, objectName, bucketName));
    return MinioUtils.removeResult();
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
    return MinioUtils.removeResult().handle(r -> {
      List<Item> items = Utils.itrToList(results)
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
    return MinioUtils.removeResult();
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
  public MinioResult<ObjectWriteResponse> uploadObjects(List<SnowballObject> objects,
                                                        boolean compression,
                                                        @Nullable String stagingFilename,
                                                        @Nonnull String bucketName) {
    return uploadObjects(UploadSnowballObjectsArgs.builder()
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
  public MinioResult<ObjectWriteResponse> uploadObjects(UploadSnowballObjectsArgs.Builder builder,
                                                        @Nonnull String bucketName) {
    requireBucketExist(bucketName);
    getClient().uploadSnowballObjects(MinioUtils.newBucketArgs(builder, bucketName));
    return MinioUtils.removeResult();
  }

  /**
   * 上传对象
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
   * 上传对象
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
    return MinioUtils.removeResult();
  }

  /**
   * 上传对象
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
    return MinioUtils.removeResult().handle(r -> {
      r.setData(list);
      r.setCode(200);
      r.setMessage("SUCCESS");
    });
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
    return MinioUtils.removeResult().handle(r -> r.setData(r.isSuccessful() ? Paths.get(file).toFile() : null));
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
  public MinioResult<ObjectWriteResponse> copyObject(@Nonnull String objectName,
                                                     @Nonnull CopySource source,
                                                     @Nullable Directive taggingDirective,
                                                     @Nonnull String bucketName) {
    return copyObject(CopyObjectArgs.builder(), objectName, source, taggingDirective, bucketName);
  }

  /**
   * 拷贝一份新的对象（服务器上会多一份新的数据）
   *
   * @param builder          Builder参数
   * @param objectName       对象名
   * @param source           拷贝的源文件
   * @param taggingDirective 触发指令：拷贝或替换
   * @param bucketName       桶
   * @return 返回拷贝结果
   */
  public MinioResult<ObjectWriteResponse> copyObject(CopyObjectArgs.Builder builder,
                                                     @Nonnull String objectName,
                                                     @Nonnull CopySource source,
                                                     @Nullable Directive taggingDirective,
                                                     @Nonnull String bucketName) {
    getClient().copyObject(MinioUtils.newObjectArgs(builder
            .source(source)
            .taggingDirective(taggingDirective != null ? taggingDirective : Directive.COPY)
        , objectName, bucketName));
    return MinioUtils.removeResult();
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
    return MinioUtils.removeResult();
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
    return MinioUtils.removeResult().handle(r -> r.setData(r.isSuccessful()));
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
    return MinioUtils.removeResult();
  }

}
