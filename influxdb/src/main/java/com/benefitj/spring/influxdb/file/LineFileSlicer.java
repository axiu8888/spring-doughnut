package com.benefitj.spring.influxdb.file;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 行协议文件
 */
public class LineFileSlicer {

  /**
   * 文件最大50MB
   */
  public static final long MAX_SIZE = (1024 << 10) * 50;
  /**
   * 默认缓存目录
   */
  private static final File DEFAULT_CACHE_DIR;

  static {
    Properties p = System.getProperties();
    String tmpDir = p.getProperty("java.io.tmpdir");
    DEFAULT_CACHE_DIR = new File(tmpDir, "influxdb/lines/");
  }

  /**
   * 缓存目录
   */
  private File cacheDir = DEFAULT_CACHE_DIR;
  /**
   * 文件最大长度
   */
  private long maxSize = MAX_SIZE;
  /**
   * 创建文件
   */
  private LineFileFactory lineFileFactory = LineFileFactory.INSTANCE;
  /**
   * line文件
   */
  private final AtomicReference<FileWriterPair> pairRef = new AtomicReference<>();
  /**
   * 监听Line文件
   */
  private LineFileListener lineFileListener = LineFileListener.DISCARD_LISTENER;
  /**
   * 上次写入时间
   */
  private long lastWriteTime = -1;

  public LineFileSlicer() {
  }

  public LineFileSlicer(File cacheDir, long maxSize) {
    this.cacheDir = cacheDir;
    this.maxSize = maxSize;
  }

  private void write0(Collection<String> lines) {
    try {
      boolean newFile;
      FileWriterPair pair = getPair(true);
      synchronized (this) {
        for (String line : lines) {
          pair.append(line).append("\n");
        }
        pair.flush();
        // 检查文件
        newFile = checkNewFile(pair);
        if (newFile) {
          this.pairRef.set(null);
        }
      }
      this.setLastWriteTime(System.currentTimeMillis());
      if (newFile) {
        pair.close();
        getLineFileListener().onHandleLineFile(pair, pair.getFile());
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 写入一行数据
   *
   * @param line 行协议数据
   */
  public void write(String line) {
    write0(Collections.singletonList(line));
  }

  /**
   * 写入行协议数据
   *
   * @param lines 行协议数据
   */
  public void write(String... lines) {
    write(Arrays.asList(lines));
  }

  /**
   * 写入行协议数据
   *
   * @param lines 行协议数据
   */
  public void write(Collection<String> lines) {
    write(lines, true);
  }

  /**
   * 写入行协议数据
   *
   * @param lines   行协议数据
   * @param overall 是否全部写入到一个文件
   */
  public void write(Collection<String> lines, boolean overall) {
    if (overall) {
      write0(lines);
    } else {
      for (String line : lines) {
        write0(Collections.singletonList(line));
      }
    }
  }

  public long getLastWriteTime() {
    return lastWriteTime;
  }

  public void setLastWriteTime(long lastWriteTime) {
    this.lastWriteTime = lastWriteTime;
  }

  /**
   * 检查文件是否满足新文件条件
   */
  public boolean checkNewFile() {
    return checkNewFile(getPair());
  }

  /**
   * 检查文件是否满足新文件条件
   */
  public boolean checkNewFile(FileWriterPair pair) {
    return pair != null && pair.length() >= getMaxSize();
  }

  /**
   * 刷新文件
   */
  public void refresh() {
    FileWriterPair pair = this.getPair();
    if (pair != null) {
      synchronized (this) {
        this.pairRef.compareAndSet(pair, null);
        pair.close();
        this.getLineFileListener().onHandleLineFile(pair, pair.getFile());
      }
    }
  }

  protected FileWriterPair getPair() {
    return getPair(false);
  }

  protected FileWriterPair getPair(boolean created) {
    FileWriterPair pair = this.pairRef.get();
    if (pair == null && created) {
      synchronized (this) {
        if ((pair = this.pairRef.get()) != null) {
          return pair;
        }
        // 创建新文件
        pair = getLineFileFactory().create(getCacheDir());
        this.pairRef.set(pair);
      }
    }
    return pair;
  }

  /**
   * 获取最后一个文件长度
   */
  public long length() {
    FileWriterPair pair = this.getPair();
    return pair != null ? pair.length() : 0L;
  }

  public File getCacheDir() {
    return cacheDir;
  }

  public void setCacheDir(File cacheDir) {
    this.cacheDir = cacheDir;
  }

  public long getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(long maxSize) {
    this.maxSize = maxSize > (1024 << 10) ? maxSize : (1024 << 10);
  }

  public LineFileFactory getLineFileFactory() {
    return lineFileFactory;
  }

  public void setLineFileFactory(LineFileFactory lineFileFactory) {
    this.lineFileFactory = (lineFileFactory != null ? lineFileFactory : LineFileFactory.INSTANCE);
  }

  public LineFileListener getLineFileListener() {
    return lineFileListener;
  }

  public void setLineFileListener(LineFileListener lineFileListener) {
    this.lineFileListener = (lineFileListener != null ? lineFileListener : LineFileListener.DISCARD_LISTENER);
  }

}
