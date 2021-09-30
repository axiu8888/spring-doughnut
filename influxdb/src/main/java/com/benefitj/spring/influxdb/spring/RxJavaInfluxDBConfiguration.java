package com.benefitj.spring.influxdb.spring;

import com.benefitj.spring.influxdb.convert.LineProtocolConverterFactory;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.template.InfluxProperty;
import com.benefitj.spring.influxdb.template.RxJavaInfluxDBTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * RxJava template
 */
@Lazy
@ConditionalOnMissingBean(RxJavaInfluxDBConfiguration.class)
@Configuration
public class RxJavaInfluxDBConfiguration {

  /**
   * 属性配置
   */
  @ConditionalOnMissingBean(InfluxProperty.class)
  @Bean
  public InfluxProperty influxDBProperty() {
    return new InfluxProperty();
  }

  /**
   * Point转换工厂
   */
  @ConditionalOnMissingBean(PointConverterFactory.class)
  @Bean
  public PointConverterFactory pointConverterFactory() {
    return PointConverterFactory.INSTANCE;
  }

  /**
   * 行协议转换工厂
   */
  @ConditionalOnMissingBean(LineProtocolConverterFactory.class)
  @Bean
  public LineProtocolConverterFactory lineProtocolConverterFactory() {
    return LineProtocolConverterFactory.INSTANCE;
  }

  /**
   * RxJavaInfluxDBTemplate
   *
   * @param property         配置
   * @param converterFactory point 转换工厂
   * @return 返回 RxJavaInfluxDBTemplate
   */
  @ConditionalOnMissingBean(RxJavaInfluxDBTemplate.class)
  @Bean
  public RxJavaInfluxDBTemplate rxJavaInfluxDBTemplate(InfluxProperty property,
                                                       PointConverterFactory converterFactory) {
    RxJavaInfluxDBTemplate template = new RxJavaInfluxDBTemplate();
    if (converterFactory != null) {
      template.setConverterFactory(converterFactory);
    }
    if (property != null) {
      template.setProperty(property);
    }
    return template;
  }

}
