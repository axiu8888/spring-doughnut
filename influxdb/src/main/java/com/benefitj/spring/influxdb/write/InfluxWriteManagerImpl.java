package com.benefitj.spring.influxdb.write;

import com.benefitj.core.EventLoop;
import com.benefitj.core.ShutdownHook;
import com.benefitj.core.local.LocalCache;
import com.benefitj.core.local.LocalCacheFactory;
import com.benefitj.spring.influxdb.file.LineFileFactory;
import com.benefitj.spring.influxdb.file.LineFileListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认的写入实现
 */
public class InfluxWriteManagerImpl implements InfluxWriteManager {

  public static final long MB = 1024 << 10;
  /**
   * executor
   */
  private ExecutorService executor = EventLoop.io();
  /**
   * 缓存文件的引用
   */
  private final List<LineFileWriter> writers = new CopyOnWriteArrayList<>();
  /**
   * 配置
   */
  private InfluxWriteProperty property;
  /**
   * 写入分派器
   */
  private WriterDispatcher writerDispatcher = WriterDispatcher.newRoundWriterDispatcher();

  /**
   * 创建line文件的工厂
   */
  private LineFileFactory lineFileFactory = LineFileFactory.INSTANCE;
  /**
   * 处理line文件的监听
   */
  private LineFileListener lineFileListener = LineFileListener.DISCARD_LISTENER;
  /**
   * 初始化状态
   */
  private volatile boolean initialized = false;
  /**
   * 线程的写入统计
   */
  private final LocalCache<AtomicInteger> localWriteCount
      = LocalCacheFactory.newCache(() -> new AtomicInteger(0));

  public InfluxWriteManagerImpl() {
  }

  public InfluxWriteManagerImpl(InfluxWriteProperty property) {
    this.property = property;
  }

  @EventListener
  protected void onAppStart(ApplicationReadyEvent event) {
    // 程序启动
    EventLoop.io().scheduleAtFixedRate(
        this::checkAndFlush, 1, 1, TimeUnit.SECONDS);
  }

  /**
   * 注册销毁时的回调钩子
   */
  protected void requireInitialized() {
    if (!initialized) {
      synchronized (this) {
        if (initialized) {
          return;
        }
        InfluxWriteProperty p = getProperty();
        int lineFileCount = p.getLineFileCount();
        for (int i = 0; i < lineFileCount; i++) {
          getWriters().add(newFileWriter(p));
        }
        ShutdownHook.register(() ->
            getWriters().forEach(LineFileWriter::close));
        initialized = true;
      }
    }
  }

  protected LineFileWriter newFileWriter(InfluxWriteProperty prop) {
    LineFileWriter writer = new LineFileWriter();
    writer.setDelay(prop.getDelay() * 1000);
    writer.setMaxSize(prop.getCacheSize() * MB);
    writer.setCacheDir(new File(prop.getCacheDir()));
    writer.setLineFileFactory(getLineFileFactory());
    writer.setLineFileListener(getLineFileListener());
    return writer;
  }

  public void offer(Runnable r) {
    getExecutor().execute(r);
  }

  public List<LineFileWriter> getWriters() {
    return this.writers;
  }

  /**
   * 写入
   *
   * @param lines 行协议数据
   */
  protected void write0(List<String> lines) {
    if (isNotEmpty(lines)) {
      requireInitialized();
      LineFileWriter writer = getWriterDispatcher().dispatch(this.getWriters());
      final AtomicInteger counter = localWriteCount.get();
      try {
        counter.incrementAndGet();
        writer.write(lines);
      } finally {
        counter.decrementAndGet();
      }
    }
  }

  /**
   * 异步保存
   *
   * @param lines 行协议数据
   */
  @Override
  public void writeAsync(List<String> lines) {
    if (isNotEmpty(lines)) {
      offer(() -> write0(lines));
    }
  }

  /**
   * 同步保存
   *
   * @param lines 行协议数据
   */
  @Override
  public void writeSync(List<String> lines) {
    write0(lines);
  }

  /**
   * 立刻保存
   */
  @Override
  public void flushNow() {
    for (LineFileWriter writer : getWriters()) {
      checkFlush(writer, true);
    }
  }

  /**
   * 调度器
   */
  @Override
  public ExecutorService getExecutor() {
    return executor;
  }

  /**
   * 检查是否可上传数据
   */
  protected void checkAndFlush() {
    for (LineFileWriter writer : getWriters()) {
      checkFlush(writer, false);
    }
  }

  /**
   * 检查并上传数据
   *
   * @param writer 行协议文件写入对象
   * @param force  是否强制上传
   */
  public void checkFlush(LineFileWriter writer, boolean force) {
    if (writer.isWritable(force)) {
      writer.refresh();
    }
  }

  @Override
  public int currentWriteCount() {
    AtomicInteger counter = this.localWriteCount.get();
    return counter.get();
  }

  @Override
  public InfluxWriteProperty getProperty() {
    return property;
  }

  @Override
  public void setLineFileFactory(LineFileFactory factory) {
    this.lineFileFactory = factory;
  }

  @Override
  public LineFileFactory getLineFileFactory() {
    return this.lineFileFactory;
  }

  @Override
  public void setLineFileListener(LineFileListener listener) {
    this.lineFileListener = listener;
  }

  @Override
  public LineFileListener getLineFileListener() {
    return this.lineFileListener;
  }

  @Override
  public WriterDispatcher getWriterDispatcher() {
    return writerDispatcher;
  }

  @Override
  public void setWriterDispatcher(WriterDispatcher writerDispatcher) {
    this.writerDispatcher = writerDispatcher;
  }

  protected static boolean isNotEmpty(Collection<?> c) {
    return c != null && !c.isEmpty();
  }

}
