package com.benefitj.athenapdf.spring;

import com.benefitj.athenapdf.AthenapdfHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.benefitj.athenapdf.api")
@EnableConfigurationProperties
@Configuration
public class AthenapdfConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public AthenapdfHelper athenapdfHelper() {
    return AthenapdfHelper.INSTANCE;
  }

}
