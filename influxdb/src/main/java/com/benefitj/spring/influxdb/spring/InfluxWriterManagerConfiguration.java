package com.benefitj.spring.influxdb.spring;

import com.benefitj.core.Unit;
import com.benefitj.spring.influxdb.template.RxJavaInfluxDBTemplate;
import com.benefitj.spring.influxdb.write.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * InfluxDB行协议文件写入配置
 */
@EnableRxJavaInfluxDB
@Configuration
public class InfluxWriterManagerConfiguration {

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
    return LineFileFactory.newFactory();
  }

  /**
   * 行协议文件监听
   */
  @ConditionalOnMissingBean
  @Bean
  public LineFileListener lineFileListener(RxJavaInfluxDBTemplate template) {
    return LineFileListener.newLineFileListener(template);
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
  public InfluxWriterManager influxWriterManager(LineFileFactory lineFileFactory,
                                                LineFileListener lineFileListener,
                                                InfluxWriteProperty property) {
    File cacheDir = new File(property.getCacheDir());
    InfluxWriterManager manager = new InfluxWriterManager(cacheDir);
    manager.setDelay(property.getDelay() * 1000);
    manager.setMaxSize(property.getCacheSize() * Unit.MB);
    manager.setFileFactory(lineFileFactory);
    manager.setFileListener(lineFileListener);
    return manager;
  }

  /**
   * 程序启动时自动写入的监听
   *
   * @param template rxjava template
   * @param property         配置属性
   * @return 监听实例
   */
  @ConditionalOnMissingBean
  @Bean
  public InfluxAutoWriteStarter influxAutoWriteListener(RxJavaInfluxDBTemplate template,
                                                        InfluxWriteProperty property) {
    return new InfluxAutoWriteStarter(template, property);
  }

}
