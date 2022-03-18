package com.benefitj.system;

import com.alibaba.fastjson.JSON;
import com.benefitj.core.DUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.core.TryCatchUtils;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.AnnotationSearcher;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.mvc.mapping.MappingAnnotationMetadata;
import com.benefitj.system.controller.vo.ApiDetail;
import com.benefitj.system.controller.vo.ApiModule;
import com.benefitj.system.utils.TableExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class SystemApplication {
  public static void main(String[] args) {
    SpringApplication.run(SystemApplication.class, args);

//    exportTableExcel();
  }

  @OnAppStart
  public void onAppStart() {
//    SysUserService service = SpringCtxHolder.getBean(SysUserService.class);
//    service.insert(SysUserEntity.builder()
//        .id(IdUtils.uuid())
//        .orgId("123456")
//        .name("张三")
//        .birthday(TimeUtils.toDate(1984, 5, 22))
//        .gender(GenderEnum.MALE)
//        .locked(Boolean.FALSE)
//        .active(Boolean.TRUE)
//        .build());


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


    String contextPath = SpringCtxHolder.getServerContextPath();
    AnnotationSearcher mappingSearcher = SpringCtxHolder.getBean("mappingSearcher");

//    Map<String, List<String>> map = new HashMap<>();
//    for (AnnotationMetadata metadata : mappingSearcher.getMetadatas()) {
//      MappingAnnotationMetadata am = (MappingAnnotationMetadata) metadata;
//      map.computeIfAbsent(am.getTargetClass().getSimpleName() + "[" + String.join(", ", am.getApi().tags()) + "]", type -> new LinkedList<>())
//          .addAll(am.getApiDescriptors()
//              .stream()
//              .flatMap(ad -> ad.getPaths()
//                  .stream()
//                  .map(path -> path + "["
//                      + "(" + String.join(", ", ad.getMethods()) + ")"
//                      + ", " + ad.getApiOperation().value()
//                      + "]")
//              )
//              .collect(Collectors.toList()));
//    }
//    System.err.println(JSON.toJSONString(map));


    Map<Class<?>, ApiModule> apiModules = new LinkedHashMap<>();
    for (AnnotationMetadata metadata : mappingSearcher.getMetadatas()) {
      MappingAnnotationMetadata am = (MappingAnnotationMetadata) metadata;
      apiModules.computeIfAbsent(am.getTargetClass(), s -> ApiModule.builder()
          .contextPath(contextPath)
          .className(am.getTargetClass().getSimpleName())
          .apiTags(Arrays.asList(am.getApi().tags()))
          .baseUrls(List.of(am.getBaseMapping().value()))
          .apiDetails(new LinkedList<>())
          .build())
          .getApiDetails()
          .addAll(am.getApiDescriptors()
              .stream()
              .map(ad -> ApiDetail.builder()
                  .className(am.getTargetClass().getSimpleName())
                  .methodName(am.getMethod().getName())
                  .apiOperation(ad.getApiOperation().value())
                  .paths(ad.getPaths())
                  .methods(ad.getMethods())
                  .baseUrls(List.of(am.getBaseMapping().value()))
                  .build())
              .collect(Collectors.toList()));
    }
    System.err.println(JSON.toJSONString(apiModules.values()));

  }

  public static void exportTableExcel() {
    // 导出excel
    TableExcelUtils.export(new File("D:/system.xlsx"), "com.benefitj.system.model");
  }

}
