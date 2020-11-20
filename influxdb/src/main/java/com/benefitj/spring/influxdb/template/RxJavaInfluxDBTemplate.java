package com.benefitj.spring.influxdb.template;

import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import org.influxdb.RxJavaInfluxDB;
import org.influxdb.RxJavaInfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * InfluxDBTemplate的RxJava实现
 */
public class RxJavaInfluxDBTemplate extends BaseInfluxDBTemplate<RxJavaInfluxDB, Flowable<QueryResult>> {

  public RxJavaInfluxDBTemplate() {
  }

  public RxJavaInfluxDBTemplate(InfluxProperty property) {
    super(property);
  }

  public RxJavaInfluxDBTemplate(InfluxProperty property, PointConverterFactory converterFactory) {
    super(property, converterFactory);
  }

  /**
   * 查询字符串
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   */
  public Flowable<String> queryString(Query query, int chunkSize) {
    return getInfluxDB().queryString(query, chunkSize, BackpressureStrategy.BUFFER);
  }

  /**
   * 创建新的InfluxDB实现对象
   *
   * @param url      URL 地址
   * @param username 用户名
   * @param password 密码
   * @param client   OkHttp的Builder
   * @return 返回新创建的InfluxDB对象
   */
  @Override
  public RxJavaInfluxDB createInfluxDB(String url, String username, String password, OkHttpClient.Builder client) {
    return RxJavaInfluxDBFactory.connect(url, username, password, client);
  }

  /**
   * Executes a query against the database.
   *
   * @param query the query to execute
   * @return a List of time series data matching the query
   */
  @Override
  public Flowable<QueryResult> query(Query query) {
    return getInfluxDB().query(query, BackpressureStrategy.BUFFER);
  }

  /**
   * Executes a query against the database.
   *
   * @param query    the query to execute
   * @param timeUnit the time unit to be used for the query
   * @return a List of time series data matching the query
   */
  @Override
  public Flowable<QueryResult> query(Query query, TimeUnit timeUnit) {
    return getInfluxDB().query(query, timeUnit, BackpressureStrategy.BUFFER);
  }

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @return a List of time series data matching the query
   */
  @Override
  public Flowable<QueryResult> query(Query query, int chunkSize) {
    return getInfluxDB().query(query, chunkSize, BackpressureStrategy.BUFFER);
  }

  /**
   * deprecated: see {@link #query(Query, int)}
   * <p>
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consuming that the List of time series data matching the query
   */
  @Deprecated
  @Override
  public void query(Query query, int chunkSize, Consumer<QueryResult> consumer) {
    getInfluxDB().query(query, chunkSize, consumer);
  }
}
