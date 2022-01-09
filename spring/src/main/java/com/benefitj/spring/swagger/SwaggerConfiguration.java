package com.benefitj.spring.swagger;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

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

//  @Bean
//  public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
//    return new BeanPostProcessor() {
//
//      @Override
//      public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if (bean instanceof WebMvcRequestHandlerProvider) {
//          customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
//        }
//        return bean;
//      }
//
//      private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
//        List<T> copy = mappings.stream()
//            .filter(mapping -> mapping.getPatternParser() == null)
//            .collect(Collectors.toList());
//        mappings.clear();
//        mappings.addAll(copy);
//      }
//
//      @SuppressWarnings("unchecked")
//      private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
//        try {
//          Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
//          field.setAccessible(true);
//          return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
//        } catch (IllegalArgumentException | IllegalAccessException e) {
//          throw new IllegalStateException(e);
//        }
//      }
//    };
//  }

}

