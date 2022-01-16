package com.benefitj.system.service;

import com.benefitj.system.model.SysAccountEntity;
import com.benefitj.system.model.SysUserEntity;
import com.benefitj.system.utils.Const;
import com.benefitj.spring.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 */
@Service
public class RedisService {

  @Autowired
  private StringRedisTemplate redisTemplate;


  public String getValue(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public <T> T getValue(String key, Class<T> type) {
    String value = getValue(key);
    return StringUtils.isNotBlank(value) ? JsonUtils.fromJson(value, type) : null;
  }

  public void setValue(String key, String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public void setValue(String key, String value, long timeout, TimeUnit unit) {
    redisTemplate.opsForValue().set(key, value, timeout, unit);
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }

  /**
   * 获取用户缓存
   *
   * @param userId 用户ID
   * @return 返回用户ID
   */
  public SysUserEntity getUser(String userId) {
    return getValue(Const.keyUserInfo(userId), SysUserEntity.class);
  }

  /**
   * 缓存用户信息
   *
   * @param user 用户信息
   */
  public void setUser(SysUserEntity user) {
    setValue(Const.keyUserInfo(user.getId()), JsonUtils.toJson(user), 1, TimeUnit.HOURS);
  }

  /**
   * 删除缓存的用户
   *
   * @param userId 用户ID
   */
  public void deleteUser(String userId) {
    delete(Const.keyUserInfo(userId));
  }

  /**
   * 获取用户账号
   *
   * @param username 用户名
   * @return 返回缓存的账号
   */
  public SysAccountEntity getAccount(String username) {
    return getValue(Const.keyAccount(username), SysAccountEntity.class);
  }

  /**
   * 缓存账号
   *
   * @param account 账号
   */
  public void setAccount(SysAccountEntity account) {
    setValue(Const.keyAccount(account.getUsername()), JsonUtils.toJson(account), 1, TimeUnit.HOURS);
  }

  /**
   * 删除缓存的账号
   *
   * @param username 用户名
   */
  public void deleteAccount(String username) {
    delete(Const.keyAccount(username));
  }

  /**
   * 通过患者ID获取用户
   *
   * @param userId 患者ID
   * @return 返回账号
   */
  public SysAccountEntity getAccountByUserId(String userId) {
    String username = getValue(Const.keyUsername(userId));
    return StringUtils.isNotBlank(username) ? getAccount(username) : null;
  }

  public void setUsername(String userId, String username) {
    setValue(Const.keyUsername(userId), username, 1, TimeUnit.HOURS);
  }


}
