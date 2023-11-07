package com.benefitj.spring.influxdb.spring;

import com.benefitj.core.Utils;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import com.benefitj.spring.influxdb.write.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * InfluxDB行协议文件写入配置
 */
@Configuration
public class InfluxWriteManagerConfiguration {

  /**
   * 配置属性
   */
  @ConfigurationProperties(prefix = "spring.influxdb.writer")
  @ConditionalOnMissingBean
  @Bean
  public InfluxWriteOptions influxWriteOptions() {
    return new InfluxWriteOptions();
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
  public LineFileListener lineFileListener(InfluxTemplate template) {
    return LineFileListener.newLineFileListener(template);
  }

  /**
   * 保存InfluxDB数据的管理实例
   *
   * @param lineFileFactory  行协议文件工厂
   * @param lineFileListener 行协议文件监听
   * @param options         配置属性
   * @return 写入管理类实例
   */
  @ConditionalOnMissingBean
  @Bean
  public InfluxWriteManager influxWriterManager(LineFileFactory lineFileFactory,
                                                LineFileListener lineFileListener,
                                                InfluxWriteOptions options) {
    File cacheDir = new File(options.getCacheDir());
    InfluxWriteManager manager = new InfluxWriteManager(cacheDir);
    manager.setDelay(options.getDelay() * 1000);
    manager.setMaxSize(options.getCacheSize() * Utils.MB);
    manager.setFileFactory(lineFileFactory);
    manager.setFileListener(lineFileListener);
    return manager;
  }

  /**
   * 程序启动时自动写入的监听
   *
   * @param template rxjava template
   * @param options 配置属性
   * @return 监听实例
   */
  @ConditionalOnMissingBean
  @Bean
  public AppStarterAutoWriter influxAutoWriteListener(InfluxTemplate template,
                                                      InfluxWriteOptions options) {
    return new AppStarterAutoWriter(template, options);
  }

}
