package com.benefitj.spring.influxdb.template;


import com.benefitj.spring.influxdb.dto.QueryResult;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 查询结果消费者
 */
public interface QueryHandler<C extends ValueConverter> {

  Predicate<QueryResult.Result> RESULT_FILTER = r -> r != null && r.getSeries() != null;
  Function<QueryResult.Result, Stream<QueryResult.Series>> SERIES_STREAM = r -> r.getSeries().stream();

  /**
   * 获取 SeriesConverter
   */
  C getConverter();

  /**
   * 查询开始
   */
  default void onQueryStart() {
    // 开始
  }

  /**
   * 处理一个数据
   */
  default void onResultNext(QueryResult result) {
    result.getResults()
        .stream()
        .filter(RESULT_FILTER)
        .flatMap(SERIES_STREAM)
        .forEach(this::iterator);
  }

  /**
   * 查询完成
   */
  default void onQueryComplete() {
    // 完成
  }

  /**
   * 查询被取消时
   */
  default void onQueryCancel() {
    // 被取消
  }

  /**
   * 查询出现异常
   */
  default void onQueryError(Throwable e) {
    e.printStackTrace();
  }


  /***********************************************************************/

  /**
   * 当新的Series开始时
   *
   * @param series 序列
   * @param c      转换器对象
   */
  default void onSeriesStart(QueryResult.Series series, C c) {
    // ~
  }

  /**
   * 迭代Series的下一个值
   *
   * @param values   值
   * @param c        转换器对象
   * @param position 当前值的位置
   */
  void onSeriesNext(List<Object> values, C c, int position);

  /**
   * 迭代Series的完成
   *
   * @param series 序列
   * @param c      转换器对象
   */
  default void onSeriesComplete(QueryResult.Series series, C c) {
    // ~
  }

  /**
   * 迭代序列数据
   */
  default void iterator(QueryResult.Series series) {
    try {
      final C c = this.getConverter();
      int position = c.getPosition();
      c.setSeries(series);
      c.setPosition(0);
      this.onSeriesStart(series, c);
      int size = c.getValues().size();
      for (int i = 0; i < size; i++) {
        c.setPosition(i);
        this.onSeriesNext(c.getValueList(i), c, i);
      }
      this.onSeriesComplete(series, c);
      c.setPosition(position);
    } catch (Exception e) {
      onQueryError(e);
    }
  }
}

