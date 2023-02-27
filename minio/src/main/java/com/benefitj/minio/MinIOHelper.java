package com.benefitj.minio;

import com.benefitj.core.SingletonSupplier;
import io.minio.BucketArgs;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.SetBucketPolicyArgs;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

public class MinIOHelper {

  private static final SingletonSupplier<MinIOHelper> singleton = SingletonSupplier.of(MinIOHelper::new);

  public static MinIOHelper get() {
    return singleton.get();
  }

  /**
   * 构建桶的参数
   *
   * @param bucketName 桶
   * @return 返回参数
   */
  public <B extends BucketArgs, BA extends BucketArgs.Builder<BA, B>> B newBucketArgs(BA builder, @Nonnull String bucketName) {
    return newBucketArgs(builder, bucketName, null, null, null);
  }

  /**
   * 构建桶的参数
   *
   * @param bucketName 桶
   * @return 返回参数
   */
  public <B extends BucketArgs, BA extends BucketArgs.Builder<BA, B>> B newBucketArgs(BA builder,
                                                                                      @Nonnull String bucketName,
                                                                                      @Nullable String region) {
    return newBucketArgs(builder, bucketName, region, null, null);
  }

  /**
   * 检查桶是否存在的参数
   *
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 返回参数
   */
  public <B extends BucketArgs, BA extends BucketArgs.Builder<BA, B>> B newBucketArgs(BA builder,
                                                                                      @Nonnull String bucketName,
                                                                                      @Nullable String region,
                                                                                      @Nullable Map<String, String> extraHeaders,
                                                                                      @Nullable Map<String, String> extraQueryParams) {
    return new ArgsBuilder<>(builder)
        .set(b -> b.bucket(bucketName))
        .set(b -> b.region(region), StringUtils.isNotBlank(region))
        .set(b -> b.extraHeaders(extraHeaders), extraHeaders != null && !extraHeaders.isEmpty())
        .set(b -> b.extraQueryParams(extraQueryParams), extraQueryParams != null && !extraQueryParams.isEmpty())
        .get()
        .build();
  }

  /**
   * 检查桶是否存在的参数
   *
   * @param bucketName 桶
   * @return 返回参数
   */
  public BucketExistsArgs bucketExistsArgs(@Nonnull String bucketName) {
    return newBucketArgs(BucketExistsArgs.builder(), bucketName);
  }

  /**
   * 创建桶
   *
   * @param bucketName 桶
   * @return 返回参数
   */
  public MakeBucketArgs makeBucket(@Nonnull String bucketName) {
    return newBucketArgs(MakeBucketArgs.builder(), bucketName);
  }

  /**
   * 设置桶策略
   *
   * @param bucketName 桶
   * @param config     策略
   * @return 返回参数
   */
  public SetBucketPolicyArgs setBucketPolicy(String bucketName, String config) {
    return newBucketArgs(new SetBucketPolicyArgs.Builder().config(config), bucketName);
  }

  public static class ArgsBuilder<T> {

    private final T target;

    public ArgsBuilder(T target) {
      this.target = target;
    }

    public ArgsBuilder<T> set(Consumer<T> consumer) {
      return set(consumer, true);
    }

    public ArgsBuilder<T> set(Consumer<T> consumer, boolean match) {
      if (match) {
        consumer.accept(get());
      }
      return this;
    }

    public T get() {
      return target;
    }

  }

}
