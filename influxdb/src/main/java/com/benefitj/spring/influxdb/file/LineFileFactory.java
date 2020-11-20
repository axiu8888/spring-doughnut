package com.benefitj.spring.influxdb.file;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件工厂
 */
public interface LineFileFactory {

  LineFileFactory INSTANCE = LineFileFactory::newFile;

  /**
   * 创建新文件
   *
   * @param dir 目录
   * @return 返回创建的文件
   */
  FileWriterPair create(File dir);


  static FileWriterPair newFile(File dir) {
    String name = UUID.randomUUID().toString().replace("-", "");
    File file = new File(dir, name + ".line");
    try {
      File parent = file.getParentFile();
      if (!parent.exists()) {
        parent.mkdirs();
      }
      file.createNewFile();
      return new FileWriterPair(file);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
