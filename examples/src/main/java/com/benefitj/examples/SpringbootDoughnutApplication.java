package com.benefitj.examples;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.ratelimiter.EnableRedisRateLimiter;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.influxdb.spring.EnableInfluxDB;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.TimeUnit;


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

//    AppStateHook.registerStart(evt -> testEhcache());
    AppStateHook.registerStart(evt -> testCaffeine());
  }

//  static void testEhcache() {
//    log.info("Creating cache manager via XML resource");
//    ClassPathResource cpr = new ClassPathResource("ehcache.xml");
//    Configuration config = new XmlConfiguration(CatchUtils.tryThrow(cpr::getURL));
//    try (CacheManager cacheManager = CacheManagerBuilder.newCacheManager(config)) {
//      cacheManager.init();
//
//      Cache<Long, String> cache = cacheManager.getCache("cache", Long.class, String.class);
//      log.info("Putting to cache");
//      cache.put(1L, "da one!");
//      String value = cache.get(1L);
//      log.info("Retrieved '{}'", value);
//      log.info("Closing cache manager");
//    }
//    log.info("Exiting");
//  }

  static void testCaffeine() {
    Cache<Object, Object> cache = Caffeine.newBuilder()
        //初始数量
        .initialCapacity(10)
        //最大条数
        .maximumSize(10)
        //PS：expireAfterWrite和expireAfterAccess同时存在时，以expireAfterWrite为准。
        // 最后一次写操作后经过指定时间过期
        .expireAfterWrite(1, TimeUnit.SECONDS)
        // 最后一次读或写操作后经过指定时间过期
        .expireAfterAccess(1, TimeUnit.SECONDS)
        //监听缓存被移除
        .removalListener((key, val, removalCause) -> { })
        //记录命中
        .recordStats()
        .build();

    cache.put("1","张三");
    System.out.println(cache.getIfPresent("1"));
    System.out.println(cache.get("2",o -> "默认值"));
  }

}
