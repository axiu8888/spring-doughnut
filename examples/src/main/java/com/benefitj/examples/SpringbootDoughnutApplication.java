package com.benefitj.examples;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.ratelimiter.EnableRedisRateLimiter;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageListener;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@EnableSpringCtxInit
@PropertySource(value = {
    "classpath:/swagger-api-info.properties",
//    "classpath:/mongodb.properties",
}, encoding = "utf-8")
@EnableSwaggerApi
//@EnableRedisMessageListener
//@EnableRedisRateLimiter         // redis RateLimiter
@EnableHttpLoggingHandler       // HTTP请求日志
@EnableEventBusPoster           // eventbus
//@EnableAthenapdf                // PDF
//@EnableInfluxdb                 // InfluxDB
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
    //AppStateHook.registerStart(evt -> onStart(evt));
  }

//  private static void onStart(ApplicationReadyEvent evt) {
//    log.info("{} start...", evt.getApplicationContext().getApplicationName());
//
//    EventLoop.asyncIO(() -> {
//
//      MongoTemplate primaryMongoTemplate = SpringCtxHolder.getBean("primaryMongoTemplate", MongoTemplate.class);
//      log.info("primaryMongoTemplate: {}", primaryMongoTemplate.getCollectionNames());
//      MongoTemplate secondaryMongoTemplate = SpringCtxHolder.getBean("secondaryMongoTemplate", MongoTemplate.class);
//      log.info("secondaryMongoTemplate: {}", secondaryMongoTemplate.getCollectionNames());
//      MongoTemplate tertiaryMongoTemplate = SpringCtxHolder.getBean("tertiaryMongoTemplate", MongoTemplate.class);
//      log.info("tertiaryMongoTemplate: {}", tertiaryMongoTemplate.getCollectionNames());
//
//      log.info("secondary -> primary  ==>: {}", JSON.toJSONString(transferTo(secondaryMongoTemplate, primaryMongoTemplate)));
//      log.info("tertiary -> primary  ==>: {}", JSON.toJSONString(transferTo(tertiaryMongoTemplate, primaryMongoTemplate)));
//
//      // 结束...
//      EventLoop.asyncIO(() -> System.exit(0), 1000);
//
//    }, 5, TimeUnit.SECONDS);
//  }
//
//  static List<List<Object>> transferTo(MongoTemplate from, MongoTemplate to) {
//    List<List<Object>> list = new LinkedList<>();
//    from.getCollectionNames()
//        .forEach(name -> {
//          log.info("[start] from: {}, name: {}", from, name);
//          List<DocumentId> ids = from.find(new BasicQuery("{}"), DocumentId.class, name);
//          log.info("[end] from: {}, name: {}, ids: {}", from, name, ids.stream().map(id -> id._id).toList());
//          ids
//              .stream()
//              .map(id -> id._id)
//              .forEach(id -> {
//                JSONObject json = from.findById(id, JSONObject.class, name);
//                to.save(json, name);
//              });
//          list.add(Arrays.asList(name, ids.size(), ids.stream()
//              .map(r -> r._id)
//              .collect(Collectors.toList())));
//        });
//    return list;
//  }
//
//
//  @Data
//  public static class DocumentId {
//
//    String _id;
//
//  }
//
//  @EventListener(ApplicationReadyEvent.class)
//  public void onAppStart() {
//    RedisTemplate<String, Object> redisTemplate = SpringCtxHolder.getBean("redisTemplate");
//    List<String> keys = keys(redisTemplate, "collector:bind_record:*", 1);
//    log.info("keys: {}", keys);
//
//    EventLoop.asyncIO(() -> System.exit(0), 1000);
//  }
//
//  public List<String> keys(RedisTemplate<String, ?> redisTemplate, String pattern, int size) {
//    ScanOptions options = ScanOptions.scanOptions()
//        .match(pattern)
//        .count(size)
//        .build();
//    List<String> keys = new LinkedList<>();
//    try (Cursor<String> cursor = redisTemplate.scan(options);) {
//      while (cursor.hasNext()) {
//        keys.add(cursor.next());
//        if (keys.size() >= size) {
//          break;
//        }
//      }
//    }
//    return keys;
//  }

}
