package com.benefitj.provider.mqttaclredis;

import com.benefitj.spring.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.util.List;

/**
 * 根据配置文件初始化
 */
public class MqttAclRedisInitializer {

  private MqttAclStringRedisTemplate redisTemplate;
  private MqttAclRedisOptions properties;

  public MqttAclRedisInitializer() {
  }

  @EventListener
  public void onAppStart(ApplicationReadyEvent event) {
    String path = properties.getInitFile();
    if (StringUtils.isNotBlank(path)) {
      File file = new File(path);
      if (!file.exists() || file.length() < 2) {
        return;
      }

      try {
        List<MqttAcl> aclList = JsonUtils.fromJson(file, new TypeReference<List<MqttAcl>>() {});
        for (MqttAcl acl : aclList) {
          if (StringUtils.isNoneBlank(acl.getUsername(), acl.getPassword())) {
            // 保存密码
            getRedisTemplate().setPassword(acl.getUsername(), acl.getPassword());
            if (StringUtils.isNotBlank(acl.getSalt())) {
              getRedisTemplate().setSalt(acl.getUsername(), acl.getSalt());
            }
            // 授权
            if (acl.getTopics() != null && acl.getTopics().length > 0) {
              getRedisTemplate().setAcl(acl.getUsername(), acl.getTopics());
            }
          }
        }
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
  }

  public MqttAclStringRedisTemplate getRedisTemplate() {
    return redisTemplate;
  }

  public void setRedisTemplate(MqttAclStringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public MqttAclRedisOptions getProperties() {
    return properties;
  }

  public void setProperties(MqttAclRedisOptions properties) {
    this.properties = properties;
  }
}
