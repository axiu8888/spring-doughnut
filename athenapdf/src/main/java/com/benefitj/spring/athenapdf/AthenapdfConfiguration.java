package com.benefitj.spring.athenapdf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties
@Configuration
public class AthenapdfConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public AthenapdfHelper athenapdfHelper() {
    return AthenapdfHelper.INSTANCE;
  }

}
