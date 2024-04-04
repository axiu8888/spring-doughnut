package com.benefitj.spring.athenapdf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties
@Configuration
public class AthenapdfConfiguration {

  @Value("#{@environment['spring.athenapdf.container'] ?: 'arachnysdocker/athenapdf'}")
  String athenapdfContainer;

  @ConditionalOnMissingBean
  @Bean
  public AthenapdfHelper athenapdfHelper() {
    AthenapdfHelper helper = AthenapdfHelper.get();
    helper.setContainer(athenapdfContainer);
    return helper;
  }

  @ConditionalOnMissingBean
  @Bean
  public ApiController athenapdfController() {
    return new ApiController();
  }

}
