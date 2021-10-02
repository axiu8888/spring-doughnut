package com.benefitj.spring.influxdb.template;

import com.benefitj.spring.influxdb.InfluxDBUtils;
import com.benefitj.spring.influxdb.convert.ConverterFactory;
import com.benefitj.spring.influxdb.convert.PointConverter;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.ContinuousQuery;
import com.benefitj.spring.influxdb.dto.InfluxCountInfo;
import com.benefitj.spring.influxdb.dto.InfluxFieldKey;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.influxdb.BasicInfluxDB;
import org.influxdb.InfluxDB;
import org.influxdb.QueryObserver;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.Preconditions;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.lang.annotation.RetentionPolicy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * InfluxDB template
 *
 * @param <Q>
 */
public interface InfluxDBTemplate<I extends BasicInfluxDB, Q> extends ConverterFactory<Point>, InitializingBean {

  /**
   * 创建新的InfluxDB实现对象
   *
   * @param url      URL 地址
   * @param username 用户名
   * @param password 密码
   * @param client   OkHttp的Builder
   * @return 返回新创建的InfluxDB对象
   */
  I createInfluxDB(String url, String username, String password, OkHttpClient.Builder client);

  /**
   * 获取InfluxDB
   */
  I getInfluxDB();

  /**
   * InfluxDB的配置
   */
  InfluxDBProperty getProperty();

  /**
   * Point转换器 {@link PointConverter}
   */
  default <T> PointConverter<T> getPointConverter(Class<T> type) {
    return getConverterFactory().getConverter(type);
  }

  /**
   * Point转换器工厂
   *
   * @return {@link PointConverterFactory}
   */
  PointConverterFactory getConverterFactory();

  /**
   * 数据库名
   */
  String getDatabase();

  /**
   * 缓存策略
   */
  String getRetentionPolicy();

  /**
   * 批量写入时的一致性类型
   */
  InfluxDB.ConsistencyLevel getConsistencyLevel();

  /**
   * 写入
   *
   * @param record 记录
   * @param <T>    数据类型
   */
  <T> void write(final T record);

  /**
   * 写入
   *
   * @param records 记录
   * @param <T>     数据类型
   */
  <T> void write(final T[] records);

  /**
   * 写入
   *
   * @param records 记录
   * @param <T>     数据类型
   */
  <T> void write(final List<T> records);

  /**
   * 写入文件
   *
   * @param batchPoints 行协议数据的文件
   */
  default void write(File batchPoints) {
    write(getDatabase(), getRetentionPolicy(), getConsistencyLevel(), batchPoints);
  }

  /**
   * 写入文件
   *
   * @param database        数据库
   * @param retentionPolicy 缓存策略
   * @param batchPoints     批量写入的文件
   */
  default void write(String database, String retentionPolicy, File batchPoints) {
    write(database, retentionPolicy, getConsistencyLevel(), batchPoints);
  }

  /**
   * 写入
   *
   * @param batchPoints 批量写入的文件
   */
  default void write(RequestBody batchPoints) {
    this.write(getDatabase(), getRetentionPolicy(), getConsistencyLevel(), batchPoints);
  }

  /**
   * 写入
   *
   * @param database        数据库
   * @param retentionPolicy 保留策略
   * @param consistency     一致性类型
   * @param batchPoints     批量写入的文件
   */
  void write(String database, String retentionPolicy, InfluxDB.ConsistencyLevel consistency, File batchPoints);

  /**
   * 写入
   *
   * @param database        数据库
   * @param retentionPolicy 保留策略
   * @param consistency     一致性类型
   * @param batchPoints     批量写入的请求
   */
  void write(String database, String retentionPolicy, InfluxDB.ConsistencyLevel consistency, RequestBody batchPoints);

  /**
   * new query object
   *
   * @param query query script
   * @return new query
   */
  default Query createQuery(String query) {
    return new Query(query, getDatabase());
  }

  /**
   * Executes a query against the database.
   *
   * @param query the query to execute
   * @return a List of time series data matching the query
   */
  default Q query(final String query) {
    return query(createQuery(query));
  }

  /**
   * Executes a query against the database.
   *
   * @param query the query to execute
   * @return a List of time series data matching the query
   */
  Q query(final Query query);

  /**
   * Executes a query against the database.
   *
   * @param query    the query to execute
   * @param timeUnit the time unit to be used for the query
   * @return a List of time series data matching the query
   */
  default Q query(final String query, final TimeUnit timeUnit) {
    return query(createQuery(query), timeUnit);
  }

  /**
   * Executes a query against the database.
   *
   * @param query    the query to execute
   * @param timeUnit the time unit to be used for the query
   * @return a List of time series data matching the query
   */
  Q query(final Query query, final TimeUnit timeUnit);

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @return a List of time series data matching the query
   */
  default Q query(String query, int chunkSize) {
    return query(createQuery(query), chunkSize);
  }

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @return a List of time series data matching the query
   */
  Q query(Query query, int chunkSize);

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consumer
   */
  default void query(String query, int chunkSize, Consumer<QueryResult> consumer) {
    query(createQuery(query), chunkSize, consumer);
  }

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consumer
   */
  void query(Query query, int chunkSize, Consumer<QueryResult> consumer);

  /**
   * query
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consumer
   */
  default void query(String query, int chunkSize, QueryObserver<String> consumer) {
    query(createQuery(query), chunkSize, consumer);
  }

  /**
   * query
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consumer
   */
  void query(Query query, int chunkSize, QueryObserver<String> consumer);

  /**
   * post query for result
   *
   * @param query
   * @return
   */
  QueryResult postQuery(String query);

  /**
   * post query for result
   *
   * @param query
   * @return
   */
  QueryResult postQuery(String db, String query);

  /**
   * Ping the database.
   *
   * @return the response of the ping execution
   */
  Pong ping();

  /**
   * Return the version of the connected database.
   *
   * @return the version String, otherwise unknown
   */
  String version();

  /**
   * 统计
   *
   * @param measurement 表
   * @param column      列
   * @param startTime   开始时间
   * @param endTime     结束时间
   * @return 返回统计信息
   */
  default InfluxCountInfo queryCountInfo(String measurement, String column, long startTime, long endTime) {
    return queryCountInfo(measurement, column, startTime, endTime, null);
  }

  /**
   * 统计
   *
   * @param measurement 表
   * @param column      列
   * @param startTime   开始时间
   * @param endTime     结束时间
   * @return 返回统计信息
   */
  InfluxCountInfo queryCountInfo(String measurement, String column, long startTime, long endTime, String condition);

  /**
   * create new database
   *
   * @param database database name
   * @return result
   */
  default QueryResult createDatabase(String database) {
    Preconditions.checkNonEmptyString(database, "database");
    String createDatabaseQuery = Query.encode(String.format("CREATE DATABASE \"%s\"", database));
    return postQuery(createDatabaseQuery);
  }

  /**
   * 获取全部的持续查询
   */
  default List<ContinuousQuery> getContinuousQueries() {
    return getContinuousQueries(null);
  }

  /**
   * 获取全部的持续查询
   */
  default List<ContinuousQuery> getContinuousQueries(String database) {
    String query = "SHOW CONTINUOUS QUERIES";
    return getResults(query)
        .stream()
        .filter(r -> r != null && r.getSeries() != null)
        .flatMap(r -> r.getSeries().stream())
        .filter(s -> InfluxDBUtils.isNotBlank(database) || s.getName().equals(database))
        .filter(s -> s.getValues() != null)
        .flatMap(s -> s.getValues()
            .stream()
            .map(values -> {
              ContinuousQuery continuousQuery = new ContinuousQuery();
              continuousQuery.setDatabase(s.getName());
              continuousQuery.setName(String.valueOf(values.get(0)));
              continuousQuery.setQuery(String.valueOf(values.get(1)));
              return continuousQuery;
            }))
        .collect(Collectors.toList());
  }

  /**
   * obtain all retention policies
   *
   * @return return retention policies
   */
  default List<RetentionPolicy> getRetentionPolicies() {
    return getRetentionPolicies(getDatabase());
  }

  /**
   * obtain all retention policies
   *
   * @param db database name
   * @return return retention policies
   */
  default List<RetentionPolicy> getRetentionPolicies(String db) {
    QueryResult queryResult = postQuery(db, "SHOW RETENTION POLICIES ON " + db);
    return mapperTo(queryResult, RetentionPolicy.class);
  }

  /**
   * 获取 MEASUREMENT 的 TAG keys
   *
   * @param measurement MEASUREMENT
   * @return return TAG list
   */
  default List<String> getTagKeys(String measurement) {
    return getTagKeys(getDatabase(), measurement);
  }

  /**
   * 获取 MEASUREMENT 的 TAG keys
   *
   * @param db          database name
   * @param measurement MEASUREMENT
   * @return return TAG list
   */
  default List<String> getTagKeys(String db, String measurement) {
    QueryResult showTagKeyResult = postQuery(db, "SHOW TAG KEYS FROM " + measurement);
    return getObjectsStream(showTagKeyResult)
        .flatMap(Collection::stream)
        .flatMap(o -> Stream.of(String.valueOf(o)))
        .collect(Collectors.toList());
  }

  /**
   * 获取 TAG values
   *
   * @param measurement measurements
   * @param tagKey      tag key
   * @return return TAG key value list
   */
  default List<String> getTagValues(String measurement, String tagKey) {
    return getTagValues(getDatabase(), measurement, tagKey);
  }

  /**
   * 获取 TAG values
   *
   * @param db          database name
   * @param measurement measurements
   * @param tagKey      tag key
   * @return return TAG key value list
   */
  default List<String> getTagValues(String db, String measurement, String tagKey) {
    final String sql = "SHOW TAG VALUES FROM \"" + measurement + "\" WITH KEY = \"" + tagKey + "\"";
    QueryResult queryResult = postQuery(db, sql);
    return getObjectsStream(queryResult)
        .flatMap(values -> Stream.of((String) values.get(1)))
        .collect(Collectors.toList());
  }

  /**
   * 获取 TAG values MAP
   *
   * @param measurement measurement
   * @return return TAG values Map
   */
  default Map<String, List<String>> getTagValuesMap(String measurement) {
    return getTagValuesMap(getDatabase(), measurement);
  }

  /**
   * 获取 TAG values MAP
   *
   * @param db          database name
   * @param measurement measurement
   * @return return TAG values Map
   */
  default Map<String, List<String>> getTagValuesMap(String db, String measurement) {
    final List<String> tagKeys = getTagKeys(db, measurement);
    final Map<String, List<String>> tagValuesMap = new LinkedHashMap<>();
    for (String tagKey : tagKeys) {
      List<String> values = getTagValues(db, measurement, tagKey);
      tagValuesMap.put(tagKey, values);
    }
    return tagValuesMap;
  }

  /**
   * 
   *
   * @param db
   * @param measurement
   * @param tagName
   */
  default void deleteTagValues(String db, String measurement, String tagName) {
    // ~
  }

  /**
   * obtain measurements
   *
   * @return return measurement list
   */
  default List<String> getMeasurements() {
    return getMeasurements(getDatabase());
  }

  /**
   * obtain measurements
   *
   * @param db database name
   * @return return measurement list
   */
  default List<String> getMeasurements(String db) {
    QueryResult showMeasurementResult = postQuery(db, "SHOW MEASUREMENTS ON " + db);
    return getObjectsStream(showMeasurementResult)
        .flatMap(Collection::stream)
        .flatMap(o -> Stream.of((String) o))
        .collect(Collectors.toList());
  }

  /**
   * Obtain field key map
   *
   * @param retentionPolicy retention policy
   * @param measurement     measurement
   * @return return field key map
   */
  default Map<String, InfluxFieldKey> getFieldKeyMap(String retentionPolicy, String measurement) {
    return getFieldKeyMap(getDatabase(), retentionPolicy, measurement);
  }

  /**
   * Obtain field key map
   *
   * @param db              database name
   * @param retentionPolicy retention policy
   * @param measurement     measurement
   * @return return field key map
   */
  default Map<String, InfluxFieldKey> getFieldKeyMap(String db, String retentionPolicy, String measurement) {
    return getFieldKeyMap(db, retentionPolicy, measurement, false);
  }

  /**
   * Obtain field key map
   *
   * @param db              database name
   * @param retentionPolicy retention policy
   * @param measurement     measurement
   * @param containTags     contains tag
   * @return return field key map
   */
  default Map<String, InfluxFieldKey> getFieldKeyMap(String db, String retentionPolicy, String measurement, boolean containTags) {
    final String sql = "SHOW FIELD KEYS FROM \"" + retentionPolicy + "\".\"" + measurement + "\"";
    QueryResult queryResult = postQuery(db, sql);
    List<InfluxFieldKey> fieldKeys = getObjectsStream(queryResult)
        .flatMap(values -> {
          InfluxFieldKey InfluxFieldKey = new InfluxFieldKey.Builder()
              .setColumn((String) values.get(0))
              .setFieldType(com.benefitj.spring.influxdb.dto.InfluxFieldKey.getFieldType((String) values.get(1)))
              .build();
          return Stream.of(InfluxFieldKey);
        })
        .collect(Collectors.toList());

    if (containTags) {
      List<String> tagKeys = getTagKeys(db, measurement);
      fieldKeys.addAll(tagKeys.stream()
          .flatMap(tag -> {
            InfluxFieldKey InfluxFieldKey = new InfluxFieldKey.Builder()
                .setColumn(tag)
                .setFieldType(String.class)
                .setTag(true)
                .build();
            return Stream.of(InfluxFieldKey);
          })
          .collect(Collectors.toList()));
    }

    if (!fieldKeys.isEmpty()) {
      fieldKeys.add(new InfluxFieldKey.Builder()
          .setColumn("time")
          .setFieldType(String.class)
          .setTag(true)
          .setTimestamp(true)
          .build());
    }

    if (fieldKeys.isEmpty()) {
      return Collections.emptyMap();
    }
    final Map<String, InfluxFieldKey> fieldKeyMap = new ConcurrentHashMap<>();
    for (InfluxFieldKey InfluxFieldKey : fieldKeys) {
      fieldKeyMap.put(InfluxFieldKey.getColumn(), InfluxFieldKey);
    }
    return fieldKeyMap;
  }


  default Stream<List<Object>> getObjectsStream(QueryResult queryResult) {
    return getResults(queryResult)
        .stream()
        .filter(r -> r.getSeries() != null)
        .flatMap(r -> r.getSeries().stream())
        .flatMap(s -> s.getValues().stream());
  }

  /**
   * 检查 QueryResult
   */
  default boolean checkResult(QueryResult result) {
    List<QueryResult.Result> results = result.getResults();
    return result.getError() == null && (results != null && !results.isEmpty());
  }

  /**
   * get QueryResult.Result list
   */
  default List<QueryResult.Result> getResults(QueryResult result) {
    return checkResult(result) ? result.getResults() : Collections.emptyList();
  }

  /**
   * get QueryResult.Result list
   */
  default List<QueryResult.Result> getResults(String query) {
    QueryResult queryResult = postQuery(query);
    return getResults(queryResult);
  }

  /**
   * get QueryResult.Result list
   */
  default List<QueryResult.Result> getResults(String db, String query) {
    QueryResult queryResult = postQuery(db, query);
    return getResults(queryResult);
  }

}
