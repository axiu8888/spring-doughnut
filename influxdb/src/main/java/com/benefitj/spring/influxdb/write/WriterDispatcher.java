package com.benefitj.spring.influxdb.write;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 写入文件的分排器
 */
public interface WriterDispatcher {

  /**
   * 分派
   */
  LineFileWriter dispatch(List<LineFileWriter> writers);

  /**
   * 轮训
   */
  static WriterDispatcher newRoundWriterDispatcher() {
    return new RoundWriterDispatcher();
  }

  /**
   * 轮训
   */
  class RoundWriterDispatcher implements WriterDispatcher {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public LineFileWriter dispatch(List<LineFileWriter> writers) {
      int index = counter.getAndIncrement();
      if (index >= writers.size()) {
        counter.compareAndSet(index, 0);
      }
      return writers.get(index % writers.size());
    }

  }

}
