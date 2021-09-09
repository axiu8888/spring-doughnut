package com.benefitj.provider.mqttaclredis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MqttAclStringRedisTemplate extends StringRedisTemplate {

  /**
   * 获取HashMap中的值
   *
   * @param name 名称
   * @param key  键
   * @param <T>  值类型
   * @return 返回值
   */
  public <T> T getHashValue(String name, String key) {
    return (T) opsForHash().get(name, key);
  }

  public <T> void putHashValue(String name, String key, T value) {
    opsForHash().put(name, key, value);
  }

  public void putAllHashValue(String name, Map<String, ?> map) {
    opsForHash().putAll(name, map);
  }

  public String userKey(String username) {
    return "mqtt_user:" + username;
  }

  /**
   * 获取密码
   *
   * @param username 用户名
   * @return 密码
   */
  public String getPassword(String username) {
    return getHashValue(userKey(username), "password");
  }

  /**
   * 设置密码
   *
   * @param username 用户名
   * @param password 密码
   */
  public void setPassword(String username, String password) {
    putHashValue(userKey(username), "password", password);
  }

  /**
   * 设置盐
   *
   * @param username 用户名
   * @return 返回盐
   */
  public String getSalt(String username) {
    return getHashValue(userKey(username), "salt");
  }

  /**
   * 设置盐
   *
   * @param username 用户名
   * @param salt     盐
   */
  public void setSalt(String username, String salt) {
    putHashValue(userKey(username), "salt", salt);
  }

  /**
   * 删除用户
   *
   * @param username 用户名
   */
  public void deleteUsername(String username) {
    delete(userKey(username));
  }

  public String aclKey(String name) {
    return "mqtt_acl:" + name;
  }

  /**
   * 设置ACL允许的主题
   *
   * @param username 用户名
   * @param topics   主题
   */
  public void setAcl(String username, String... topics) {
    if (topics.length <= 0) {
      return;
    }
    Map<String, String> map = new LinkedHashMap<>(topics.length);
    for (String topic : topics) {
      if (StringUtils.isNotBlank(topic)) {
        map.put(topic, "1");
      }
    }
    putAllHashValue(aclKey(username), map);
  }

  /**
   * 获取授权的主题
   *
   * @param username 用户名
   * @return 返回允许的主题
   */
  public List<String> getAcl(String username) {
    return opsForHash().entries(aclKey(username))
        .keySet()
        .stream()
        .map(o -> o != null ? o.toString() : "")
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.toList());
  }

  /**
   * 删除授权的topic
   *
   * @param username 用户名
   */
  public void deleteAcl(String username) {
    delete(aclKey(username));
  }

}
