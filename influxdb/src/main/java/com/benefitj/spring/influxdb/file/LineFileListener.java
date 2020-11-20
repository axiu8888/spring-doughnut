package com.benefitj.spring.influxdb.file;

import java.io.File;

/**
 * 监听
 */
public interface LineFileListener {

  /**
   * 处理读取的数据包
   *
   * @param pair 文件
   * @param file 行协议文件
   */
  void onHandleLineFile(FileWriterPair pair, File file);

  /**
   * 空监听
   */
  LineFileListener DISCARD_LISTENER = (pair, file) -> {};

}
