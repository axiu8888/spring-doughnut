package com.benefitj.spring.minio;

import com.benefitj.core.local.LocalCache;
import com.benefitj.core.local.LocalCacheFactory;

import javax.annotation.Nonnull;

/**
 * 缓存
 */
public interface ResultCache {

  static ResultCache create() {
    return create(LocalCacheFactory.newCache(() -> MinioResult.fail("FAIL")));
  }

  static ResultCache create(LocalCache<MinioResult> cache) {
    return () -> cache;
  }

  LocalCache<MinioResult> getCache();

  /**
   * 获取请求结果
   */
  default <T> MinioResult<T> getResult() {
    return getCache().get();
  }

  /**
   * 获取并移除一个请求结果
   */
  @Nonnull
  default <T> MinioResult<T> removeResult() {
    return getCache().getAndRemove();
  }

}
