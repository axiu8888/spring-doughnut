package com.benefitj.spring.influxdb.write;

import com.benefitj.spring.influxdb.file.LineFileFactory;
import com.benefitj.spring.influxdb.file.LineFileListener;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认的写入实现
 */
public class SimpleInfluxWriteManager implements InfluxWriteManager {

  public static final long MB = 1024 << 10;
  /**
   * executor
   */
  private ExecutorService executor;
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
  private final ThreadLocal<AtomicInteger> localWriteCount = ThreadLocal.withInitial(() -> new AtomicInteger(0));

  public SimpleInfluxWriteManager() {
  }

  public SimpleInfluxWriteManager(InfluxWriteProperty property) {
    this.property = property;
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
          LineFileWriter writer = newFileWriter(p);
          getWriters().add(writer);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            getWriters().forEach(LineFileWriter::close)));
        initialized = true;
      }
    }
  }

  protected LineFileWriter newFileWriter(InfluxWriteProperty property) {
    LineFileWriter writer = new LineFileWriter();
    writer.setDelay(property.getDelay() * 1000);
    writer.setMaxSize(property.getCacheSize() * MB);
    writer.setCacheDir(new File(property.getCacheDir()));
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
    ExecutorService e = this.executor;
    if (e == null) {
      synchronized (this) {
        if ((e = this.executor) == null) {
          InfluxWriteProperty property = getProperty();
          ThreadFactory factory = new DefaultThreadFactory("io-", "-influxdb-");
          this.executor = e = Executors.newScheduledThreadPool(property.getThreadCount(), factory);
        }
      }
    }
    return e;
  }

  /**
   * 检查是否可上传数据
   */
  @Override
  public void checkAndFlush() {
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

  /**
   * The default thread factory
   */
  public static class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public DefaultThreadFactory() {
      this("pool-", "-thread-");
    }

    public DefaultThreadFactory(String prefix, String suffix) {
      SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() :
          Thread.currentThread().getThreadGroup();
      namePrefix = prefix + poolNumber.getAndIncrement() + suffix;
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(group, r,
          namePrefix + threadNumber.getAndIncrement(), 0);
      if (t.isDaemon()) {
        t.setDaemon(false);
      }
      if (t.getPriority() != Thread.NORM_PRIORITY) {
        t.setPriority(Thread.NORM_PRIORITY);
      }
      return t;
    }
  }

}
