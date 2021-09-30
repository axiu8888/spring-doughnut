package com.benefitj.spring.influxdb.spring;

import com.benefitj.spring.influxdb.file.LineFileFactory;
import com.benefitj.spring.influxdb.file.LineFileListener;
import com.benefitj.spring.influxdb.template.InfluxDBTemplate;
import com.benefitj.spring.influxdb.write.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

/**
 * InfluxDB行协议文件写入配置
 */
@Lazy
@ConditionalOnMissingBean(InfluxWriteManagerConfiguration.class)
@Import(InfluxDBAutoCheckConfiguration.class)
@Configuration
public class InfluxWriteManagerConfiguration {

  /**
   * 配置属性
   */
  @ConditionalOnMissingBean
  @Bean
  public InfluxWriteProperty influxWriteProperty() {
    return new InfluxWriteProperty();
  }

  /**
   * 行协议文件工厂
   */
  @ConditionalOnMissingBean
  @Bean
  public LineFileFactory lineFileFactory() {
    return LineFileFactory.INSTANCE;
  }

  /**
   * 行协议文件监听
   */
  @ConditionalOnMissingBean
  @Bean
  public LineFileListener lineFileListener(InfluxDBTemplate influxDBTemplate) {
    return new InfluxLineFileListener(influxDBTemplate);
  }


  /**
   * 保存InfluxDB数据的管理实例
   *
   * @param lineFileFactory  行协议文件工厂
   * @param lineFileListener 行协议文件监听
   * @param property         配置属性
   * @return 写入管理类实例
   */
  @ConditionalOnMissingBean
  @Bean
  public InfluxWriteManager influxWriteManager(LineFileFactory lineFileFactory,
                                               LineFileListener lineFileListener,
                                               InfluxWriteProperty property) {
    SimpleInfluxWriteManager manager = new SimpleInfluxWriteManager(property);
    manager.setLineFileFactory(lineFileFactory);
    manager.setLineFileListener(lineFileListener);
    return manager;
  }

  /**
   * 程序启动时自动写入的监听
   *
   * @param influxDBTemplate rxjava influxDBTemplate
   * @param property         配置属性
   * @return 监听实例
   */
  @ConditionalOnMissingBean
  @Bean
  public InfluxAutoWriteStarter influxAutoWriteListener(InfluxDBTemplate influxDBTemplate,
                                                        InfluxWriteProperty property) {
    return new InfluxAutoWriteStarter(influxDBTemplate, property);
  }

}
