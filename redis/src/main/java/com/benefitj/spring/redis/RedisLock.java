
package com.benefitj.spring.redis;

import org.springframework.integration.redis.util.RedisLockRegistry;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * redis分布式锁
 */
public class RedisLock {

  private static final long DEFAULT_EXPIRE_UNUSED = 60000L;

  protected RedisLockRegistry redisLockRegistry;
  /**
   * 过期时间
   */
  protected long expireUnused = DEFAULT_EXPIRE_UNUSED;

  public RedisLock(RedisLockRegistry redisLockRegistry) {
    this.redisLockRegistry = redisLockRegistry;
  }

  public void lock(String lockKey) {
    Lock lock = obtainLock(lockKey);
    lock.lock();
  }

  public boolean tryLock(String lockKey) {
    Lock lock = obtainLock(lockKey);
    return lock.tryLock();
  }

  public boolean tryLock(String lockKey, long milliseconds) {
    return tryLock(lockKey, milliseconds, TimeUnit.MILLISECONDS);
  }

  public boolean tryLock(String lockKey, long time, TimeUnit unit) {
    Lock lock = obtainLock(lockKey);
    try {
      return lock.tryLock(time, unit);
    } catch (InterruptedException e) {
      return false;
    }
  }

  public void unlock(String lockKey) {
    unlock(lockKey, getExpireUnused());
  }

  public void unlock(String lockKey, long expireUnused) {
    try {
      Lock lock = obtainLock(lockKey);
      lock.unlock();
      redisLockRegistry.expireUnusedOlderThan(expireUnused);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  private Lock obtainLock(String lockKey) {
    return redisLockRegistry.obtain(lockKey);
  }

  public long getExpireUnused() {
    return expireUnused;
  }

  public void setExpireUnused(long expireUnused) {
    this.expireUnused = expireUnused;
  }
}
