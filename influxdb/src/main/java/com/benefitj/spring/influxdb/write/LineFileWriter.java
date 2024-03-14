package com.benefitj.spring.influxdb.write;

import com.benefitj.core.file.slicer.SliceFileWriter;

import java.io.File;
import java.nio.charset.Charset;

public class LineFileWriter extends SliceFileWriter {
  /**
   * 创建时间
   */
  private long createTime = System.currentTimeMillis();

  public LineFileWriter(File file) {
    super(file);
  }

  public LineFileWriter(File file, Charset charset) {
    super(file, charset);
  }

  public LineFileWriter(File file, Charset charset, boolean append) {
    super(file, charset, append);
  }

  public LineFileWriter(File file, boolean append) {
    super(file, append);
  }

  public long getCreateTime() {
    return createTime;
  }

}
