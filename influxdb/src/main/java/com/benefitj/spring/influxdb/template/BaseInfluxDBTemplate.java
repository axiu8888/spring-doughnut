package com.benefitj.spring.influxdb.template;

import com.benefitj.spring.influxdb.InfluxDBUtils;
import com.benefitj.spring.influxdb.convert.Converter;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.InfluxCountInfo;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.influxdb.BasicInfluxDB;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.QueryObserver;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.BasicAuthInterceptor;
import org.springframework.util.Assert;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 抽象的InfluxDBTemplate实现
 */
public abstract class BaseInfluxDBTemplate<I extends BasicInfluxDB, Q> implements InfluxDBTemplate<I, Q> {
  /**
   * InfluxDB
   */
  private I influxDB;
  /**
   * property
   */
  private InfluxDBProperty property;

  private HttpLoggingInterceptor httpLogging = new HttpLoggingInterceptor();

  private PointConverterFactory converterFactory = PointConverterFactory.INSTANCE;
  /**
   * SimpleDateFormat
   */
  private static final ThreadLocal<Map<String, SimpleDateFormat>> sdfUtcMap = ThreadLocal.withInitial(WeakHashMap::new);

  public BaseInfluxDBTemplate() {
  }

  public BaseInfluxDBTemplate(InfluxDBProperty property) {
    this.property = property;
  }

  public BaseInfluxDBTemplate(InfluxDBProperty property, PointConverterFactory converterFactory) {
    this.property = property;
    this.converterFactory = converterFactory;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      // 创建数据库
      createDatabase(getDatabase());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public <T> Point convert(T t) {
    return getConverterFactory().convert(t);
  }

  @Override
  public <T> List<Point> convert(T[] items) {
    return getConverterFactory().convert(items);
  }

  @Override
  public <T> List<Point> convert(Collection<T> items) {
    return getConverterFactory().convert(items);
  }

  public void setProperty(InfluxDBProperty property) {
    this.property = property;
  }

  @Override
  public InfluxDBProperty getProperty() {
    return this.property;
  }

  public void setConverterFactory(PointConverterFactory converterFactory) {
    this.converterFactory = converterFactory;
  }

  /**
   * 获取Point转换器工厂
   *
   * @return PointConverterFactory
   */
  @Override
  public PointConverterFactory getConverterFactory() {
    return this.converterFactory;
  }

  @Override
  public I getInfluxDB() {
    I db = this.influxDB;
    if (db == null) {
      final InfluxDBProperty prop = getProperty();
      Assert.notNull(prop, "InfluxDBProperty are required");
      synchronized (this) {
        if ((db = this.influxDB) != null) {
          return db;
        }

        final OkHttpClient.Builder client = new OkHttpClient.Builder()
            .connectTimeout(prop.getConnectTimeout(), TimeUnit.SECONDS)
            .writeTimeout(prop.getWriteTimeout(), TimeUnit.SECONDS)
            .readTimeout(prop.getReadTimeout(), TimeUnit.SECONDS)
            .addInterceptor(new BasicAuthInterceptor(prop.getUsername(), prop.getPassword()))
            .addNetworkInterceptor(httpLogging.setLevel(prop.getLogLevel()));

        db = createInfluxDB(prop.getUrl(), prop.getUsername(), prop.getPassword(), client);
        db.setDatabase(prop.getDatabase());
        db.setRetentionPolicy(prop.getRetentionPolicy());
        db.setConsistency(InfluxDB.ConsistencyLevel.ALL);

        if (prop.isGzip()) {
          db.enableGzip();
        }
        if (prop.isEnableBatch()) {
          db.enableBatch(BatchOptions.DEFAULTS.actions(prop.getBatchActions()));
        }
        this.influxDB = db;
      }
    }
    return db;
  }

  @Override
  public String getDatabase() {
    return getProperty().getDatabase();
  }

  @Override
  public String getRetentionPolicy() {
    return getProperty().getRetentionPolicy();
  }

  @Override
  public InfluxDB.ConsistencyLevel getConsistencyLevel() {
    return getProperty().getConsistencyLevel();
  }

  /**
   * 创建新的 PointConverter 对象
   *
   * @param type bean类型
   * @return 返回 PointConverter 对象
   */
  @Override
  public <T> Converter<T, Point> getConverter(Class<T> type) {
    return getConverterFactory().getConverter(type);
  }

  /**
   * 转换成bean对象
   *
   * @param result 查询的结果集
   * @param type   bean类型
   * @return 返回解析的对象
   */
  @Override
  public <T> List<T> mapperTo(QueryResult result, Class<T> type) {
    return getConverterFactory().mapperTo(result, type);
  }

  /**
   * 转换成行协议数据
   *
   * @param records 记录
   * @return 返回行协议数据
   */
  public <T> String lineProtocol(Collection<T> records) {
    List<String> lines = InfluxDBUtils.toLineProtocol(records);
    return String.join("\n", lines);
  }

  /**
   * Write a single measurement to the database.
   *
   * @param record the measurement to write to
   */
  @Override
  public <T> void write(T record) {
    if (record instanceof File) {
      write((File) record);
    } else {
      getInfluxDB().write(InfluxDBUtils.toLineProtocol(record));
    }
  }

  @Override
  public final <T> void write(final T[] payload) {
    write(Arrays.asList(payload));
  }

  @Override
  public <T> void write(final List<T> payload) {
    getInfluxDB().write(lineProtocol(payload));
  }

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  public void write(File batchPoints) {
    write(getDatabase(), getRetentionPolicy(), getConsistencyLevel(), batchPoints);
  }

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  public void write(String database, String retentionPolicy, File batchPoints) {
    write(database, retentionPolicy, getConsistencyLevel(), batchPoints);
  }

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  public void write(String database, String retentionPolicy, InfluxDB.ConsistencyLevel consistency, File batchPoints) {
    getInfluxDB().write(database, retentionPolicy, consistency, batchPoints);
  }

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  public void write(RequestBody batchPoints) {
    this.write(getDatabase(), getRetentionPolicy(), InfluxDB.ConsistencyLevel.ALL, batchPoints);
  }

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  public void write(String database, String retentionPolicy, InfluxDB.ConsistencyLevel consistency, RequestBody batchPoints) {
    getInfluxDB().write(database, retentionPolicy, consistency, batchPoints);
  }

  @Override
  public Pong ping() {
    return getInfluxDB().ping();
  }

  @Override
  public String version() {
    return getInfluxDB().version();
  }

  /**
   * Executes a query against the database.
   *
   * @param query the query to execute
   * @return a List of time series data matching the query
   */
  @Override
  public Q query(final String query) {
    return this.query(createQuery(query));
  }

  /**
   * Executes a query against the database.
   *
   * @param query the query to execute
   * @return a List of time series data matching the query
   */
  @Override
  public abstract Q query(final Query query);

  /**
   * Executes a query against the database.
   *
   * @param query    the query to execute
   * @param timeUnit the time unit to be used for the query
   * @return a List of time series data matching the query
   */
  @Override
  public Q query(final String query, final TimeUnit timeUnit) {
    return query(createQuery(query), timeUnit);
  }

  /**
   * Executes a query against the database.
   *
   * @param query    the query to execute
   * @param timeUnit the time unit to be used for the query
   * @return a List of time series data matching the query
   */
  @Override
  public abstract Q query(final Query query, final TimeUnit timeUnit);

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @return a List of time series data matching the query
   */
  @Override
  public Q query(String query, int chunkSize) {
    return query(createQuery(query), chunkSize);
  }

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @return a List of time series data matching the query
   */
  @Override
  public abstract Q query(Query query, int chunkSize);

  /**
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consumer
   */
  @Override
  public void query(Query query, int chunkSize, QueryObserver<String> consumer) {
    getInfluxDB().queryString(query, chunkSize, consumer);
  }

  @Override
  public QueryResult postQuery(String query) {
    return getInfluxDB().postQuery(query);
  }

  @Override
  public QueryResult postQuery(String db, String query) {
    return getInfluxDB().postQuery(db, query);
  }

  /**
   * 删除数据库
   *
   * @param measurement 表名
   * @return 返回结果
   */
  public QueryResult dropMeasurement(String measurement) {
    return dropMeasurement(getDatabase(), measurement);
  }

  /**
   * 删除数据库
   *
   * @param database    数据库名
   * @param measurement 表名
   * @return 返回结果
   */
  public QueryResult dropMeasurement(String database, String measurement) {
    return postQuery(database, "DROP MEASUREMENT " + measurement);
  }

  /**
   * 删除数据库
   *
   * @return 返回结果
   */
  public QueryResult dropDatabase() {
    return dropDatabase(getDatabase());
  }

  /**
   * 删除数据库
   *
   * @param database 数据库名
   * @return 返回结果
   */
  public QueryResult dropDatabase(String database) {
    return postQuery(database, "DROP DATABASE " + database);
  }

  /**
   * 统计
   *
   * @param measurement 表
   * @param column      列
   * @param startTime   开始时间
   * @param endTime     结束时间
   * @param condition   其他条件
   * @return 返回统计信息
   */
  @Override
  public InfluxCountInfo queryCountInfo(String measurement, String column, long startTime, long endTime, String condition) {
    String clause = String.format("FROM \"%s\" WHERE time >= '%s' AND time <= '%s' %s"
        , measurement, fmtUtcS(startTime), fmtUtcS(endTime), (condition != null ? condition : ""));
    // count
    String sql = String.format("SELECT count(%s) AS count %s", column, clause)
        // first
        + String.format("; SELECT %s AS first %s ORDER BY time ASC LIMIT 1", column, clause)
        // last
        + String.format("; SELECT %s AS last %s ORDER BY time DESC LIMIT 1", column, clause);
    final InfluxCountInfo countInfo = new InfluxCountInfo();
    countInfo.setSql(sql);
    query(createQuery(sql), 100, qr -> {
      if (qr.hasError() && !"DONE".equalsIgnoreCase(qr.getError())) {
        countInfo.setError(qr.getError());
        return;
      }

      final DefaultValueConverter c = new DefaultValueConverter();
      qr.getResults()
          .stream()
          .filter(r -> !r.hasError())
          .filter(r -> r.getSeries() != null && !r.getSeries().isEmpty())
          .flatMap(r -> r.getSeries().stream())
          .forEach(series -> {
            c.setSeries(series);
            c.setPosition(0);
            Long count = c.getLong("count");
            if (count != null) {
              countInfo.setCount(count);
            }
            Object first = c.getValue("first", null);
            if (first != null) {
              countInfo.setStartTime(c.getTime());
            }
            Object last = c.getValue("last", null);
            if (last != null) {
              countInfo.setEndTime(c.getTime());
            }
          });
    });
    return countInfo;
  }

  public SimpleDateFormat getUtcSdf(String pattern) {
    SimpleDateFormat sdf = sdfUtcMap.get().get(pattern);
    if (sdf == null) {
      sdfUtcMap.get().put(pattern, sdf = new SimpleDateFormat(pattern));
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    return sdf;
  }

  public String fmtUtcS(Object time) {
    return getUtcSdf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(time);
  }

  public String fmtUtc(Object time) {
    return getUtcSdf("yyyy-MM-dd'T'HH:mm:ss'Z'").format(time);
  }

}
