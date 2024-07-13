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
   * 延迟上传的时长，默认1分钟
   */
  private long delay = 60_000L;

  public InfluxWriteManager(File cacheDir) {
    this(cacheDir, 30 * Utils.MB);
  }

  public InfluxWriteManager(File cacheDir, long maxSize) {
    super(cacheDir, maxSize);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (getFileFactory() == null) throw new IllegalStateException("未设置文件工厂");
    if (getFileListener() == null) throw new IllegalStateException("未设置文件监听");

    // 检查是否写入数据
    EventLoop.asyncIOFixedRate(() -> {
      if (isFlushNow()) {
        flush();
      }
    }, 3, 2, TimeUnit.SECONDS);
  }

  /**
   * 是否写入数据库
   *
   * @return 返回判断结果
   */
  public boolean isFlushNow() {
    LineFileWriter w = getWriter();
    if (w == null || w.length() <= 0) return false;
    if (w.length() >= getMaxSize()) return true;
    long now = System.currentTimeMillis();
    return (now - w.getCreateTime() >= getDelay()) || (now - getLastWriteTime() >= getDelay());
  }

  public long getDelay() {
    return delay;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }
}