package com.benefitj.spring.influxdb.spring;

import com.benefitj.spring.influxdb.InfluxApiFactory;
import com.benefitj.spring.influxdb.InfluxOptions;
import com.benefitj.spring.influxdb.convert.LineProtocolConverterFactory;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.QueryResult;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import com.benefitj.spring.influxdb.template.InfluxTemplateImpl;
import com.squareup.moshi.Moshi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * InfluxTemplate
 */
@EnableConfigurationProperties
@Configuration
public class InfluxConfiguration {

  /**
   * 属性配置
   */
  @ConfigurationProperties("spring.influxdb")
  @ConditionalOnMissingBean
  @Bean
  public InfluxOptions influxDBOptions() {
    return new InfluxOptions();
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
   * Api工厂
   */
  @ConditionalOnMissingBean
  @Bean
  public InfluxApiFactory influxApiFactory() {
    return InfluxApiFactory.newInstance();
  }


  /**
   * InfluxTemplate
   *
   * @param options          配置
   * @param converterFactory point 转换工厂
   * @return 返回 RxJavaInfluxDBTemplate
   */
  @ConditionalOnMissingBean
  @Bean
  public InfluxTemplate influxTemplate(InfluxOptions options,
                                       InfluxApiFactory factory,
                                       PointConverterFactory converterFactory) {
    InfluxTemplateImpl template = new InfluxTemplateImpl();
    template.setConverterFactory(converterFactory);
    template.setOptions(options);
    template.setApi(factory.create(options));

    Moshi moshi = new Moshi.Builder().build();
    template.setJsonAdapter(moshi.adapter(QueryResult.class));

    return template;
  }

  @ConditionalOnMissingBean
  @Bean
  public InfluxInitializingBean influxInitializingBean(InfluxTemplate template) {
    return new InfluxInitializingBean(template);
  }

  @ConditionalOnWebApplication
  @ConditionalOnMissingBean
  @Bean
  public ApiController apiController() {
    return new ApiController();
  }

}
