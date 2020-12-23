package com.benefitj.spring.influxdb.write;

import com.benefitj.spring.influxdb.file.FileWriterPair;
import com.benefitj.spring.influxdb.file.LineFileSlicer;

import java.io.File;

public class LineFileWriter extends LineFileSlicer {
  /**
   * 延迟上传的时间
   */
  private long delay;
  /**
   * 初始化时间
   */
  private volatile long initializedTime = System.currentTimeMillis();

  public LineFileWriter() {
  }

  public LineFileWriter(File cacheDir, long maxSize) {
    super(cacheDir, maxSize);
  }

  public LineFileWriter(File cacheDir, long maxSize, long delay) {
    super(cacheDir, maxSize);
    this.delay = delay;
  }

  public String getName() {
    return getPair(true).getName();
  }

  public long getInitializedTime() {
    return initializedTime;
  }

  public boolean isWritable(boolean force) {
    long length = length();
    if (length <= 0) {
      return false;
    }
    if (force) {
      return true;
    }
    if (length >= getMaxSize()) {
      return true;
    }
    long now = System.currentTimeMillis();
    long delay = getDelay();
    return (now - getInitializedTime() >= delay) || (now - getLastWriteTime() >= delay);
  }

  public long getDelay() {
    return delay;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }

  public void close() {
    FileWriterPair pair = getPair();
    if (pair != null) {
      pair.close();
    }
  }
}
