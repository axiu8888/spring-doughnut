package com.benefitj.spring.influxdb.spring;

import com.benefitj.spring.influxdb.convert.LineProtocolConverterFactory;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.template.InfluxDBProperty;
import com.benefitj.spring.influxdb.template.RxJavaInfluxDBTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RxJava template
 */
@Configuration
public class RxJavaInfluxDBConfiguration {

  /**
   * 属性配置
   */
  @ConditionalOnMissingBean
  @Bean
  public InfluxDBProperty influxDBProperty() {
    return new InfluxDBProperty();
  }

  /**
   * Point转换工厂
   */
  @ConditionalOnMissingBean
  @Bean
  public PointConverterFactory pointConverterFactory() {
    return PointConverterFactory.INSTANCE;
  }

  /**
   * 行协议转换工厂
   */
  @ConditionalOnMissingBean
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
  @ConditionalOnMissingBean
  @Bean
  public RxJavaInfluxDBTemplate rxJavaInfluxDBTemplate(InfluxDBProperty property,
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
