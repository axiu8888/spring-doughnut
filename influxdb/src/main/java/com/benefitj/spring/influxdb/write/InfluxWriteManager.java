package com.benefitj.spring.influxdb.write;

import com.benefitj.spring.influxdb.file.LineFileFactory;
import com.benefitj.spring.influxdb.file.LineFileListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * InfluxDB写入数据的管理类
 */
public interface InfluxWriteManager {

  /**
   * 同步保存
   *
   * @param line 行协议数据
   */
  default void writeSync(String line) {
    writeSync(Collections.singletonList(line));
  }

  /**
   * 同步保存
   *
   * @param lines 行协议数据
   */
  default void writeSync(String... lines) {
    writeSync(Arrays.asList(lines));
  }

  /**
   * 同步保存
   *
   * @param lines 行协议数据
   */
  void writeSync(List<String> lines);

  /**
   * 异步保存
   *
   * @param line 行协议数据
   */
  default void writeAsync(String line) {
    writeAsync(Collections.singletonList(line));
  }

  /**
   * 异步保存
   *
   * @param lines 行协议数据
   */
  default void writeAsync(String... lines) {
    writeAsync(Arrays.asList(lines));
  }

  /**
   * 异步保存
   *
   * @param lines 行协议数据
   */
  void writeAsync(List<String> lines);

  /**
   * 立刻保存
   */
  void flushNow();

  /**
   * 检查并上传数据
   */
  void checkAndFlush();

  /**
   * 返回当前线程的写入次数
   */
  int currentWriteCount();

  /**
   * 调度器
   */
  ExecutorService getExecutor();

  /**
   * 配置属性
   */
  InfluxWriteProperty getProperty();

  /**
   * 创建文件的工厂
   *
   * @param factory 工厂类
   */
  void setLineFileFactory(LineFileFactory factory);

  /**
   * 获取创建文件的工厂
   */
  LineFileFactory getLineFileFactory();

  /**
   * 处理文件的监听
   *
   * @param listener 监听
   */
  void setLineFileListener(LineFileListener listener);

  /**
   * 获取处理文件的监听
   */
  LineFileListener getLineFileListener();

  /**
   * 处理文件的分派器
   *
   * @param dispatcher 分派器
   */
  void setWriterDispatcher(WriterDispatcher dispatcher);

  /**
   * 获取分派器
   */
  WriterDispatcher getWriterDispatcher();

}
