package com.benefitj.provider.mqttaclredis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


@EnableConfigurationProperties
@Configuration
public class MqttAclRedisConfiguration {

  @ConfigurationProperties(prefix = "spring.mqtt-acl.redis")
  @Bean("mqttAclRedisProperties")
  public MqttAclRedisOptions mqttAclRedisProperties() {
    return new MqttAclRedisOptions();
  }

  /**
   * 创建MQTT ACL的 StringRedisTemplate
   *
   * @param properties 配置
   * @return 返回 StringRedisTemplate
   */
  @ConditionalOnMissingBean(name = "mqttAclStringRedisTemplate")
  @Bean("mqttAclStringRedisTemplate")
  public MqttAclStringRedisTemplate mqttAclStringRedisTemplate(MqttAclRedisOptions properties) {
    RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
    conf.setHostName(properties.getHost());
    conf.setPort(properties.getPort());
    conf.setPassword(properties.getPassword());
    conf.setDatabase(properties.getDatabase());
    LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(conf);
    connectionFactory.afterPropertiesSet();
    MqttAclStringRedisTemplate stringRedisTemplate = new MqttAclStringRedisTemplate();
    stringRedisTemplate.setConnectionFactory(connectionFactory);
    return stringRedisTemplate;
  }

  /**
   * 文件配置初始化
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttAclRedisInitializer mqttAclRedisInitializer(MqttAclStringRedisTemplate template,
                                                         MqttAclRedisOptions properties) {
    MqttAclRedisInitializer initializer = new MqttAclRedisInitializer();
    initializer.setRedisTemplate(template);
    initializer.setProperties(properties);
    return initializer;
  }

}
