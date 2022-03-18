package com.benefitj.spring.swagger;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

/**
 * 接口文档配置
 */
@EnableOpenApi
public class SwaggerConfiguration {

  @Value("#{@environment['swagger.security.name'] ?: 'token'}")
  private String name;

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
    return new Docket(DocumentationType.OAS_30)
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
  @Bean
  public SecurityScheme securityScheme() {
    return new ApiKey(name, name, "header");
  }

  /**
   * 新增 securityContexts 保持登录状态
   */
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

}

