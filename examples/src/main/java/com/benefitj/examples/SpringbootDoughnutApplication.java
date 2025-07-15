package com.benefitj.examples;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.PlaceHolder;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;


@EnableSpringCtxInit
@PropertySource(value = {
    "classpath:/swagger-api-info.properties",
//    "classpath:/mongodb.properties",
}, encoding = "utf-8")
@EnableRedisMessageListener
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
    //AppStateHook.registerStart(evt -> terminal());
  }

  static void terminal() {
    EventLoop.asyncIO(() -> {
      try {
        System.out.println("================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================");
        //System.getenv().forEach((key, value) -> System.out.println(PlaceHolder.get().format("{}  -->: {}", key, value)));
        System.getProperties().forEach((key, value) -> System.out.println(PlaceHolder.get().format("{}  -->: {}", key, value)));
        System.out.println("================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================================");

        // Create a terminal
        Terminal terminal = TerminalBuilder.builder()
            .system(true)
            .build();

        // Create line reader
        LineReader reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .build();

        for(;;) {
          try {
            Charset charset = Charset.forName(System.getProperty("native.encoding", "UTF-8"));
            // Prompt and read input
            String inputContent = reader.readLine("JLine > ");
            // Print the result
            System.out.println("You entered: " + inputContent + ", encode: " + charset);
            final Process process = Runtime.getRuntime().exec(inputContent);
            while (process.isAlive()) {
              try {
                IOUtils.readLines(IOUtils.wrapReader(process.getInputStream(), charset), false, (line, num) -> {
                  log.info("is [{}] -->: {}", num, line);
                });
                IOUtils.readLines(IOUtils.wrapReader(process.getErrorStream(), charset), false, (line, num) -> {
                  log.info("err [{}] -->: {}", num, line);
                });
                EventLoop.sleepSecond(1);//等待1秒
              } catch (Throwable e) {
                log.error("throw: " + e.getMessage(), e);
              }
            }
          } catch (Throwable e) {
            log.error("throw: " + e.getMessage(), e);
          }
        }
      } catch (Exception e) {
        log.error("throw: " + e.getMessage(), e);
      }
    }, 5, TimeUnit.SECONDS);
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
