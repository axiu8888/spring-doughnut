package com.benefitj.spring.swagger;

import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 接口文档配置
 */
@EnableOpenApi
public class SwaggerConfiguration {

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
  public Docket docket(ApiInfo apiInfo) {
    return new Docket(DocumentationType.OAS_30)
        .useDefaultResponseMessages(false)
        .forCodeGeneration(true)
        .select()
        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo);
  }

}

