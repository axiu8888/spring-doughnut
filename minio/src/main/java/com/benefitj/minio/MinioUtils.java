package com.benefitj.minio;

import com.benefitj.core.local.InitialCallback;
import com.benefitj.core.local.LocalCache;
import com.benefitj.core.local.LocalCacheFactory;
import com.benefitj.frameworks.cglib.CGLibMethodInvoker;
import com.benefitj.frameworks.cglib.CGLibProxy;
import io.minio.BucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectArgs;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * MinIO工具
 */
public class MinioUtils {

  private static final LocalCache<MinioResult> localResult = LocalCacheFactory.newCache(new InitialCallback<MinioResult>() {
    @Override
    public MinioResult initialValue() {
      return MinioResult.fail("FAIL");
    }
  });

  /**
   * 获取请求结果
   */
  public static MinioResult getResult() {
    return localResult.get();
  }

  /**
   * 获取并移除一个请求结果
   */
  public static MinioResult removeResult() {
    return localResult.getAndRemove();
  }

  public static IMinioClient newProxy(MinioClient client) {
    return newProxy(null, new Class[]{IMinioClient.class}, new Object[]{client});
  }

  public static <T> T newProxy(@Nullable Class<?> superclass, @Nonnull Class<?>[] interfaces, @Nonnull Object[] objects) {
    Map<Method, CGLibMethodInvoker> invokers = new ConcurrentHashMap<>(20);
    return CGLibProxy.newProxy(superclass, interfaces, (obj, method, args, proxy) -> {
      MinioResult result = getResult();
      try {
        CGLibMethodInvoker invoker = invokers.get(method);
        if (invoker == null) {
          invoker = invokers.computeIfAbsent(method, m -> new CGLibMethodInvoker(obj, m, proxy, interfaces, objects));
        }
        Object returnValue = invoker.invoke(args);
        result.setData(returnValue);
        result.setCode(200);
        result.setMessage("SUCCESS");
        return returnValue;
      } catch (Exception e) {
        //throw new MinioException(e.getMessage());
        result.setCode(400);
        result.setMessage(e.getMessage());
        result.setError(e);
        return null;
      }
    });
  }

  /**
   * 构建桶的参数
   *
   * @param bucketName 桶
   * @return 返回参数
   */
  public static <B extends BucketArgs, BA extends BucketArgs.Builder<BA, B>> B newBucketArgs(BA builder,
                                                                                             @Nonnull String bucketName) {
    return newBucketArgs(builder, bucketName, null, null, null);
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
  public static <B extends BucketArgs, BA extends BucketArgs.Builder<BA, B>> B newBucketArgs(BA builder,
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
   * 构建桶的参数
   *
   * @param objectName 对象
   * @param bucketName 桶
   * @return 返回参数
   */
  public static <B extends ObjectArgs, BA extends ObjectArgs.Builder<BA, B>> B newObjectArgs(BA builder,
                                                                                             @Nonnull String objectName,
                                                                                             @Nonnull String bucketName) {
    return newObjectArgs(builder, objectName, bucketName, null, null, null);
  }

  /**
   * 构建对象的参数
   *
   * @param objectName       对象
   * @param bucketName       桶
   * @param region           域
   * @param extraHeaders     额外的请求头
   * @param extraQueryParams 额外的参数
   * @return 返回参数
   */
  public static <B extends ObjectArgs, BA extends ObjectArgs.Builder<BA, B>> B newObjectArgs(BA builder,
                                                                                             @Nonnull String objectName,
                                                                                             @Nonnull String bucketName,
                                                                                             @Nullable String region,
                                                                                             @Nullable Map<String, String> extraHeaders,
                                                                                             @Nullable Map<String, String> extraQueryParams) {
    return newBucketArgs(builder.object(objectName), bucketName, region, extraHeaders, extraQueryParams);
  }

  public static Map<String, String> mapOf(Pair<String, String>... pairs) {
    return mapOf(new LinkedHashMap<>(), pairs);
  }

  public static Map<String, String> mapOf(Map<String, String> map, Pair<String, String>... pairs) {
    for (Pair<String, String> pair : pairs) {
      map.put(pair.getKey(), pair.getValue());
    }
    return map;
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
