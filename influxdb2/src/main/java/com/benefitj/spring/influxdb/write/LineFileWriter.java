package com.benefitj.spring.influxdb.write;

import com.benefitj.core.file.slicer.SliceFileWriter;

import java.io.File;

public class LineFileWriter extends SliceFileWriter {
  /**
   * 创建时间
   */
  private long createTime = System.currentTimeMillis();

  public LineFileWriter(File file) {
    super(file);
  }

  public long getCreateTime() {
    return createTime;
  }

}
