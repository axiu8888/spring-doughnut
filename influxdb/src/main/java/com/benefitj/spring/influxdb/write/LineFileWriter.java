package com.benefitj.spring.influxdb.write;

import com.benefitj.core.file.slicer.SliceFileWriter;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class LineFileWriter extends SliceFileWriter {
  /**
   * 创建时间
   */
  private long createTime = System.currentTimeMillis();

  public LineFileWriter(File file) {
    super(file);
    setCharset(StandardCharsets.UTF_8);
  }

  public long getCreateTime() {
    return createTime;
  }

}
