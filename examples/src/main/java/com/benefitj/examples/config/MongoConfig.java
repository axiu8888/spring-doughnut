package com.benefitj.examples.config;


import com.benefitj.spring.mongo.MongoOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@EnableConfigurationProperties
//@Configuration
public class MongoConfig {

  @ConfigurationProperties(prefix = "mongodb.primary")
  @Bean(name = "primaryMongoOptions")
  public MongoOptions primaryMongoOptions() {
    return new MongoOptions();
  }

  @Primary
  @Bean(name = "primaryMongoTemplate")
  public MongoTemplate primaryMongoTemplate(MongoOptions primaryMongoOptions) {
    return new MongoTemplate(MongoOptions.createFactory(primaryMongoOptions));
  }

  @ConfigurationProperties(prefix = "mongodb.secondary")
  @Bean(name = "secondaryMongoOptions")
  public MongoOptions secondaryMongoOptions() {
    return new MongoOptions();
  }

  @Bean(name = "secondaryMongoTemplate")
  public MongoTemplate secondaryMongoTemplate(MongoOptions secondaryMongoOptions) {
    return new MongoTemplate(MongoOptions.createFactory(secondaryMongoOptions));
  }

  @ConfigurationProperties(prefix = "mongodb.tertiary")
  @Bean(name = "tertiaryMongoOptions")
  public MongoOptions tertiaryMongoOptions() {
    return new MongoOptions();
  }

  @Bean(name = "tertiaryMongoTemplate")
  public MongoTemplate tertiaryMongoTemplate(MongoOptions tertiaryMongoOptions) {
    return new MongoTemplate(MongoOptions.createFactory(tertiaryMongoOptions));
  }

}
