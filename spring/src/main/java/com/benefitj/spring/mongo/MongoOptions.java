package com.benefitj.spring.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * mongo 配置
 */
@SuperBuilder
@NoArgsConstructor
@Data
@ToString
public class MongoOptions {
  /**
   * 地址 ==>: mongodb://username:password@ip/database
   * 如 ==>: mongodb://admin:123456@localhost/test
   */
  private String uri;
  /**
   * 主机地址: localhost
   */
  @Builder.Default
  private String host = "localhost";
  /**
   * 端口: 27017
   */
  @Builder.Default
  private int port = 27017;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * 数据库名
   */
  private String database;
  /**
   * 地址：ip:port
   */
  private List<String> address;

  private long serverSelectionTimeoutMs;

  private int connectTimeoutMs;

  private int maxSize;

  private int minSize;
  /**
   * 最大等待时间(毫秒)
   */
  private long maxWaitTimeMs;
  /**
   * 最大连接空闲时间
   */
  private long maxConnectionIdleTime;


  /**
   * 创建MongoDB工厂
   *
   * @param opts 配置
   * @return 返回工厂对象
   */
  public static MongoDatabaseFactory createFactory(MongoOptions opts) {
    List<String> address = opts.getAddress();
    if ((address == null || address.isEmpty()) && StringUtils.isNotBlank(opts.getUri())) {
      return new SimpleMongoClientDatabaseFactory(opts.getUri());
    }
    if ((address == null || address.isEmpty()) && StringUtils.isNotBlank(opts.getHost())) {
      address = Collections.singletonList(opts.getHost() + ":" + opts.getPort());
    }
    //String uri = "mongodb://" + opts.getUsername() + ":" + opts.getPassword() + "@" + opts.getHost() + "/" + opts.getDatabase();
    //return new SimpleMongoClientDatabaseFactory(uri);
    List<ServerAddress> serverAddresses = (address != null ? address : Collections.<String>emptyList())
        .stream()
        .map(uri -> uri.split(":"))
        .map(uri -> new ServerAddress(uri[0], Integer.parseInt(uri[1])))
        .collect(Collectors.toList());
    MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();
    settingsBuilder
        .applyToClusterSettings(b -> {
          b.hosts(serverAddresses);
          b.serverSelectionTimeout(opts.getServerSelectionTimeoutMs(), TimeUnit.MILLISECONDS);
        })
        .applyToSocketSettings(b -> b.connectTimeout(opts.getConnectTimeoutMs(), TimeUnit.MILLISECONDS))
        .applyToConnectionPoolSettings(b -> {
          b.maxSize(opts.getMaxSize());
          b.minSize(opts.getMinSize());
          b.maxWaitTime(opts.getMaxWaitTimeMs(), TimeUnit.MILLISECONDS);
          b.maxConnectionIdleTime(opts.getMaxConnectionIdleTime(), TimeUnit.MILLISECONDS);
        });
    if (StringUtils.isNoneBlank(opts.getUsername(), opts.getPassword())) {
      settingsBuilder.credential(MongoCredential.createCredential(opts.getUsername(), opts.getDatabase(), opts.getPassword().toCharArray()));
    }
    MongoClientSettings settings = settingsBuilder.build();
    MongoClient mongoClient = MongoClients.create(settings);
    return new SimpleMongoClientDatabaseFactory(mongoClient, opts.getDatabase());
  }

}
