package com.benefitj.system;

import com.alibaba.fastjson.JSON;
import com.benefitj.core.DUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.core.TryCatchUtils;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.mvc.mapping.MappingAnnotationBeanProcessor;
import com.benefitj.spring.mvc.mapping.MappingAnnotationMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class SystemApplication {
  public static void main(String[] args) {
    SpringApplication.run(SystemApplication.class, args);
  }

  @OnAppStart
  public void onAppStart() {
//    SysUserService service = SpringCtxHolder.getBean(SysUserService.class);
//    SysUserEntity user = SysUserEntity.builder()
//        .id(IdUtils.uuid())
//        .orgId("123456")
//        .name("张三")
//        .birthday(TimeUtils.toDate(1984, 5, 22))
//        .gender(GenderEnum.MALE)
//        .locked(Boolean.FALSE)
//        .active(Boolean.TRUE)
//        .build();
//    service.insert(user);


    EventLoop.io().schedule(() -> TryCatchUtils.tryThrow(() -> {
      String ip = InetAddress.getLocalHost().getHostAddress();
      String port = SpringCtxHolder.getEnvProperty("server.port");
      String path = SpringCtxHolder.getEnvProperty("server.servlet.context-path");
      String swaggerBaseUrl = SpringCtxHolder.getEnvProperty("springfox.documentation.swagger-ui.base-url");
      swaggerBaseUrl = DUtils.withs(swaggerBaseUrl, "/", "/");
      String address = ip + ":" + port + path;
      log.info("\n---------------------------------------------------------------------------------\n\t" +
          "[ " + SpringCtxHolder.getAppName() + " ] is running! Access URLs:\n\t" +
          "Local: \t\t\thttp://localhost:" + port + path + "/\n\t" +
          "External: \t\thttp://" + address + "/\n\t" +
          "Swagger文档: \thttp://" + address + swaggerBaseUrl + "swagger-ui/index.html\n\t" +
          "knife4j文档: \thttp://" + address + "/doc.html\n" +
          "---------------------------------------------------------------------------------");
    }), 1, TimeUnit.SECONDS);


    MappingAnnotationBeanProcessor searcher = SpringCtxHolder.getBean(MappingAnnotationBeanProcessor.class);
    Map<String, List<String>> map = new HashMap<>();
    for (AnnotationMetadata metadata : searcher.getMetadatas()) {
      MappingAnnotationMetadata am = (MappingAnnotationMetadata) metadata;
      map.computeIfAbsent(am.getTargetClass().getSimpleName() + "[" + String.join(", ", am.getApi().tags()) + "]", type -> new LinkedList<>())
          .addAll(am.getDescriptors()
              .stream()
              .flatMap(ad -> ad.getPaths()
                  .stream()
                  .map(path -> path + "[(" + String.join(", ", ad.getHttpMethods()) + "), " + ad.getApiOperation().value() + "]"))
              .collect(Collectors.toList()));
    }
    System.err.println(JSON.toJSONString(map));

  }

}
