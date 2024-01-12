package com.benefitj.examples;

import com.benefitj.core.CatchUtils;
import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.ratelimiter.EnableRedisRateLimiter;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.influxdb.spring.EnableInfluxDB;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;


@PropertySource(value = "classpath:/swagger-api-info.properties", encoding = "utf-8")
@EnableSwaggerApi
@EnableRedisMessageChannel
@EnableHttpLoggingHandler       // HTTP请求日志
@EnableRedisRateLimiter         // redis RateLimiter
@EnableEventBusPoster           // eventbus
@EnableAthenapdf                // PDF
@EnableInfluxDB                   // InfluxDB
@SpringBootApplication
@Slf4j
public class SpringbootDoughnutApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringbootDoughnutApplication.class, args);
  }

  static {
    AppStateHook.register(
        evt -> log.info("app started ..."),
        evt -> log.info("app stopped ...")
    );

    AppStateHook.registerStart(evt -> testEhcache());
  }

  static void testEhcache() {
    log.info("Creating cache manager via XML resource");
    ClassPathResource cpr = new ClassPathResource("classpath:ehcache.xml");
    Configuration config = new XmlConfiguration(CatchUtils.tryThrow(cpr::getURL));
    try (CacheManager cacheManager = CacheManagerBuilder.newCacheManager(config)) {
      cacheManager.init();

      Cache<Long, String> cache = cacheManager.getCache("cache", Long.class, String.class);
      log.info("Putting to cache");
      cache.put(1L, "da one!");
      String value = cache.get(1L);
      log.info("Retrieved '{}'", value);
      log.info("Closing cache manager");
    }
    log.info("Exiting");
  }

}
