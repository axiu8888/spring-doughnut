package com.benefitj.spring.quartz;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@ConditionalOnProperty(prefix = "spring.quartz", value = "jdbc-enable", matchIfMissing = false)
@EnableConfigurationProperties
//@PropertySource("classpath:quartz-jdbc-spring.properties")
@PropertySource("classpath:quartz-spring.properties")
@Configuration
public class QuartzJdbcConfiguration {
}
