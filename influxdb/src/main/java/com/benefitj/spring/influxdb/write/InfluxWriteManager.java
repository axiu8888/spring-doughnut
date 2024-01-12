package com.benefitj.spring.influxdb.write;


import com.benefitj.core.EventLoop;
import com.benefitj.core.Utils;
import com.benefitj.core.file.slicer.FileSlicer;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 默认的写入实现
 */
public class InfluxWriteManager extends FileSlicer<LineFileWriter> implements InitializingBean {

  /**
   * 延迟上传的时长，默认5分钟
   */
  private long delay = 5 * 60_000;

  public InfluxWriteManager(File cacheDir) {
    this(cacheDir, 50 * Utils.MB);
  }

  public InfluxWriteManager(File cacheDir, long maxSize) {
    super(cacheDir, maxSize);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (getFileFactory() == null) {
      throw new IllegalStateException("未设置文件工厂");
    }
    if (getFileListener() == null) {
      throw new IllegalStateException("未设置文件监听");
    }

    // 检查是否写入数据
    EventLoop.asyncIOFixedRate(() -> {
      if (isWritable()) {
        flush();
      }
    }, 3, 1, TimeUnit.SECONDS);
  }

  /**
   * 是否写入数据库
   *
   * @return 返回判断结果
   */
  public boolean isWritable() {
    LineFileWriter writer = getWriter();
    if (writer == null || writer.length() <= 0) {
      return false;
    }
    if (writer.length() >= getMaxSize()) {
      return true;
    }
    long now = System.currentTimeMillis();
    return (now - writer.getCreateTime() >= getDelay()) || (now - getLastWriteTime() >= getDelay());
  }

  public long getDelay() {
    return delay;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }
}