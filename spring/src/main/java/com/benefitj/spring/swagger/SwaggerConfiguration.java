package com.benefitj.spring.swagger;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.core.NetworkUtils;
import com.benefitj.core.Utils;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.EnableAppStateListener;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口文档配置
 */
@ConditionalOnWebApplication
@EnableSpringCtxInit
@EnableAppStateListener
@EnableOpenApi
@Configuration
@Slf4j
public class SwaggerConfiguration {

  @Value("#{@environment['springfox.documentation.swagger.security.name'] ?: 'Authorization'}")
  private String name;

  @Value("#{@environment['springfox.documentation.swagger.doc-type'] ?: 'OAS_30'}")
  private SwaggerDocType docType = SwaggerDocType.OAS_30;

  @Value("#{@environment['app.start-print.delay'] ?: 3000L}")
  private Long delay;

  @ConditionalOnMissingBean
  @Bean
  public SwaggerApiInfo swaggerApiInfo() {
    return new SwaggerApiInfo();
  }

  /**
   * ApiInfo
   */
  @ConditionalOnMissingBean
  @Bean
  public ApiInfo apiInfo(SwaggerApiInfo info) {
    SwaggerApiInfo.Contact contact = info.getContact();
    return new ApiInfoBuilder()
        .title(info.getTitle())
        .description(info.getDescription())
        .termsOfServiceUrl(info.getTermsOfServiceUrl())
        .contact(new Contact(contact.getName(), contact.getUrl(), contact.getEmail()))
        .version(info.getVersion())
        .license(info.getLicense())
        .licenseUrl(info.getLicenseUrl())
        .build();
  }

  @ConditionalOnMissingBean
  @Bean
  public Docket docket(ApiInfo apiInfo,
                       @Autowired(required = false) List<SecurityScheme> securitySchemes,
                       @Autowired(required = false) List<SecurityContext> securityContexts) {
    return new Docket(docType != null ? docType.getType() : DocumentationType.SWAGGER_2)
        .useDefaultResponseMessages(false)
        .forCodeGeneration(true)
        .select()
        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo)
        .securitySchemes(securitySchemes)
        .securityContexts(securityContexts);
  }

  /**
   * 修复高版本的bug
   */
  @ConditionalOnMissingBean
  @Bean
  public SpringfoxFixedBug springfoxFixedBug() {
    return new SpringfoxFixedBug();
  }

  /***
   * oauth2配置
   * 需要增加swagger授权回调地址
   * http://localhost:8888/webjars/springfox-swagger-ui/o2c.html
   */
  @ConditionalOnProperty(prefix = "springfox.documentation.swagger.security", name = "nameEnable", matchIfMissing = false)
  @ConditionalOnMissingBean
  @Bean
  public SecurityScheme securityScheme() {
    return new ApiKey(name, name, "header");
  }

  /**
   * 新增 securityContexts 保持登录状态
   */
  @ConditionalOnMissingBean
  @Bean
  public SecurityContext authSecurityContext() {
    return SecurityContext.builder()
        .securityReferences(Collections.singletonList(
            new SecurityReference(name,
                new AuthorizationScope[]{
                    new AuthorizationScope("global", "accessEverything"),
                }))
        )
        .forPaths(PathSelectors.regex("^(?!auth).*$"))
        .build();
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onAppStart() {
    EventLoop.asyncIO(() -> CatchUtils.tryThrow(() -> {
      List<String> ips = NetworkUtils.getInetAddressAll()
          .stream()
          .filter(ia -> !ia.isAnyLocalAddress())
          .filter(ia -> !ia.isLoopbackAddress())
          .filter(ia -> !ia.isLinkLocalAddress())
          .filter(ia -> !ia.isMCNodeLocal())
          .filter(ia -> ia instanceof Inet4Address)
          .map(InetAddress::getHostAddress)
          .sorted((o1, o2) -> {
            if (o1.startsWith("192")) {
              return o2.startsWith("192")
                  ? o1.split("\\.")[3].compareTo(o2.split("\\.")[3])
                  : -1;
            }
            return -o1.compareTo(o2);
          })
          .collect(Collectors.toList());
      log.info("ips ==>: {}", ips);
      String ip = ips.get(0);//InetAddress.getLocalHost().getHostAddress();
      String port = SpringCtxHolder.getServerPort();
      String ctxPath = SpringCtxHolder.getServerContextPath();
      String path = Utils.isEndWiths(ctxPath, "/") ? ctxPath.substring(0, ctxPath.length() - 1) : ctxPath;
      String swaggerBaseUrl = SpringCtxHolder.getEnvProperty("springfox.documentation.swagger-ui.base-url", "");
      swaggerBaseUrl = Utils.withs(swaggerBaseUrl, "/", "/");
      String address = ip + ":" + port + path;
      log.info("\n---------------------------------------------------------------------------------\n\t" +
          "[ " + SpringCtxHolder.getAppName() + " ] is running! Access URLs:\n\t" +
          "ips: \t\t\t[" + StringUtils.join(ips, ", ") + "]\n\t" +
          "Local: \t\t\thttp://localhost:" + port + path + "\n\t" +
          "External: \t\thttp://" + address + "\n\t" +
          "Swagger文档: \thttp://" + address + swaggerBaseUrl + "swagger-ui/index.html\n\t" +
          "knife4j文档: \thttp://" + address + "/doc.html\n" +
          "---------------------------------------------------------------------------------");
    }), delay);
  }

}

