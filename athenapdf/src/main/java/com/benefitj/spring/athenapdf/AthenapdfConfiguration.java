package com.benefitj.spring.athenapdf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.benefitj.spring.athenapdf.api")
@EnableConfigurationProperties
@Configuration
public class AthenapdfConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public AthenapdfHelper athenapdfHelper() {
    return AthenapdfHelper.INSTANCE;
  }

}
