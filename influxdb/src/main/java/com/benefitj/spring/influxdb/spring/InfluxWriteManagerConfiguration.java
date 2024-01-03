package com.benefitj.spring.influxdb.spring;

import com.benefitj.core.Utils;
import com.benefitj.spring.influxdb.InfluxOptions;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import com.benefitj.spring.influxdb.write.AppStarterAutoWriter;
import com.benefitj.spring.influxdb.write.InfluxWriteManager;
import com.benefitj.spring.influxdb.write.LineFileFactory;
import com.benefitj.spring.influxdb.write.LineFileListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * InfluxDB行协议文件写入配置
 */
@Configuration
public class InfluxWriteManagerConfiguration {

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
                                                InfluxOptions options) {
    InfluxOptions.Writer writer = options.getWriter();
    File cacheDir = new File(writer.getCacheDir());
    InfluxWriteManager manager = new InfluxWriteManager(cacheDir);
    manager.setDelay(writer.getDelay() * 1000);
    manager.setMaxSize(writer.getCacheSize() * Utils.MB);
    manager.setFileFactory(lineFileFactory);
    manager.setFileListener(lineFileListener);
    manager.setCharset(StandardCharsets.UTF_8);
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
                                                      InfluxOptions options) {
    return new AppStarterAutoWriter(template, options);
  }

}
