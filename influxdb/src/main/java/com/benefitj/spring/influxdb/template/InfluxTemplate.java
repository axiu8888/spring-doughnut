package com.benefitj.spring.influxdb.template;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.ShutdownHook;
import com.benefitj.spring.influxdb.*;
import com.benefitj.spring.influxdb.convert.PointConverter;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.*;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;

import java.io.*;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * InfluxDB 模板
 */
public interface InfluxTemplate {

  MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain");

  InfluxApi getApi();

  InfluxOptions getOptions();

  /**
   * Point转换器工厂
   *
   * @return {@link PointConverterFactory}
   */
  PointConverterFactory getConverterFactory();

  /**
   * Point转换器 {@link PointConverter}
   */
  default <T> PointConverter<T> getPointConverter(Class<T> type) {
    return getConverterFactory().getConverter(type);
  }

  /**
   * 转换成bean对象
   *
   * @param result 查询的结果集
   * @param type   bean类型
   * @return 返回解析的对象
   */
  default <T> List<T> mapperTo(QueryResult result, Class<T> type) {
    return getConverterFactory().mapperTo(result, type);
  }

  /**
   * 转换成行协议数据
   *
   * @param records 记录
   * @return 返回行协议数据
   */
  default <T> String lineProtocol(Collection<T> records) {
    List<String> lines = InfluxUtils.toLineProtocol(records);
    return String.join("\n", lines);
  }

  /**
   * 数据库名
   */
  default String getDatabase() {
    return getOptions().getDatabase();
  }

  /**
   * 缓存策略
   */
  default String getRetentionPolicy() {
    return getOptions().getRetentionPolicy();
  }

  /**
   * 批量写入时的一致性类型
   */
  default InfluxApi.ConsistencyLevel getConsistencyLevel() {
    return getOptions().getConsistencyLevel();
  }

  default RequestBody wrapBody(String lineProtocol) {
    return RequestBody.create(MEDIA_TYPE_STRING, lineProtocol);
  }

  /**
   * 写入
   *
   * @param record 记录
   * @param <T>    数据类型
   */
  default <T> void write(final T record) {
    if (record instanceof String) {
      write(wrapBody((String) record));
    } else if (record instanceof File) {
      write((File) record);
    } else if (record instanceof Point) {
      write(wrapBody(((Point) record).lineProtocol()));
    } else {
      write(wrapBody(String.join("\n", lineProtocol(Collections.singleton(record)))));
    }
  }

  /**
   * 写入
   *
   * @param records 记录
   * @param <T>     数据类型
   */
  default <T> void write(final T[] records) {
    write(Arrays.asList(records));
  }

  /**
   * 写入
   *
   * @param records 记录
   * @param <T>     数据类型
   */
  default <T> void write(final List<T> records) {
    write(wrapBody(String.join("\n", lineProtocol(records))));
  }

  /**
   * 写入文件
   *
   * @param database        数据库
   * @param retentionPolicy 缓存策略
   * @param batchPoints     批量写入的文件
   */
  default void write(String database, String retentionPolicy, String... batchPoints) {
    write(database, retentionPolicy, getConsistencyLevel(), wrapBody(String.join("\n", batchPoints)));
  }

  /**
   * 写入
   *
   * @param batchPoints 行协议数据
   */
  default void write(RequestBody batchPoints) {
    write(getDatabase(), getRetentionPolicy(), getConsistencyLevel(), batchPoints);
  }

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
   * @param db              数据库
   * @param retentionPolicy 保留策略
   * @param consistency     一致性类型
   * @param batchPoints     批量写入的文件
   */
  default void write(String db, String retentionPolicy, InfluxApi.ConsistencyLevel consistency, File batchPoints) {
    write(db, retentionPolicy, consistency, RequestBody.create(batchPoints, MEDIA_TYPE_STRING));
  }

  /**
   * 写入
   *
   * @param db              数据库
   * @param retentionPolicy 保留策略
   * @param consistency     一致性类型
   * @param batchPoints     批量写入的请求
   */
  default void write(String db,
                     String retentionPolicy,
                     InfluxApi.ConsistencyLevel consistency,
                     RequestBody batchPoints) {
    write(db, retentionPolicy, TimeUnit.NANOSECONDS, consistency, batchPoints);
  }

  /**
   * 写入
   *
   * @param db              数据库
   * @param retentionPolicy 保留策略
   * @param precision       时间精度
   * @param consistency     一致性类型
   * @param batchPoints     批量写入的请求
   */
  default void write(String db,
                     String retentionPolicy,
                     TimeUnit precision,
                     InfluxApi.ConsistencyLevel consistency,
                     RequestBody batchPoints) {
    AtomicReference<Throwable> error = new AtomicReference<>();
    write(db, retentionPolicy, precision, consistency, batchPoints, response -> {
      if (!response.isSuccessful()) {
        try {
          throw new IllegalStateException(response.errorBody().string());
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    }, error::set);
    if (error.get() != null) {
      throw new IllegalStateException(error.get());
    }
  }

  /**
   * 写入
   *
   * @param db              数据库
   * @param retentionPolicy 保留策略
   * @param precision       时间精度
   * @param consistency     一致性类型
   * @param batchPoints     批量写入的请求
   * @param next            结果处理
   * @param error           错误处理
   */
  default void write(String db,
                     String retentionPolicy,
                     TimeUnit precision,
                     InfluxApi.ConsistencyLevel consistency,
                     RequestBody batchPoints,
                     Consumer<Response<ResponseBody>> next,
                     Consumer<Throwable> error) {
    precision = precision != null ? precision : TimeUnit.NANOSECONDS;
    consistency = consistency != null ? consistency : InfluxApi.ConsistencyLevel.ALL;
    getApi()
        .writePoints(db, retentionPolicy, InfluxTimeUtil.toTimePrecision(precision), consistency.value(), batchPoints)
        .subscribe(new SimpleSubscriber<Response<ResponseBody>>() {
          @Override
          public void onNext(Response<ResponseBody> response) {
            next.accept(response);
          }

          @Override
          public void onError(Throwable t) {
            error.accept(t);
          }
        });
  }

  /**
   * 查询
   *
   * @param query 查询语句
   * @return 返回查询结果
   */
  default Flowable<QueryResult> query(final String query) {
    return query(getDatabase(), query);
  }

  /**
   * 查询
   *
   * @param db    数据库
   * @param query 查询语句
   * @return 返回查询结果
   */
  default Flowable<QueryResult> query(String db, final String query) {
    return query(db, query, 1000);
  }

  /**
   * 查询
   *
   * @param query     查询语句
   * @param chunkSize 块大小
   * @return 返回查询结果
   */
  default Flowable<QueryResult> query(String query, int chunkSize) {
    return query(getDatabase(), query, chunkSize);
  }

  /**
   * 查询
   *
   * @param db        数据库
   * @param query     查询语句
   * @param chunkSize 块大小
   * @return 返回查询结果
   */
  default Flowable<QueryResult> query(String db, String query, int chunkSize) {
    return query(db, query, chunkSize, null);
  }

  /**
   * 查询
   *
   * @param db        数据库
   * @param query     查询语句
   * @param chunkSize 块大小
   * @param params    参数
   * @return 返回查询结果
   */
  Flowable<QueryResult> query(String db, String query, int chunkSize, String params);

  /**
   * 查询
   *
   * @param query 查询语句
   * @return 返回查询结果
   */
  QueryResult postQuery(String query);

  /**
   * 查询
   *
   * @param db    数据库
   * @param query 查询语句
   * @return 返回查询结果
   */
  QueryResult postQuery(String db, String query);

  /**
   * Ping the database.
   *
   * @return the response of the ping execution
   */
  default Pong ping() {
    AtomicReference<Pong> ref = new AtomicReference<>();
    long started = System.currentTimeMillis();
    getApi()
        .ping()
        .subscribe(SimpleSubscriber.create(response -> {
          Headers headers = response.headers();
          String version = "unknown";
          for (String name : headers.toMultimap().keySet()) {
            if (null != name && "X-Influxdb-Version".equalsIgnoreCase(name)) {
              version = headers.get(name);
              break;
            }
          }
          Pong pong = new Pong();
          pong.setVersion(version);
          pong.setResponseTime(System.currentTimeMillis() - started);
          ref.set(pong);
        }));
    return ref.get();
  }

  /**
   * Return the version of the connected database.
   *
   * @return the version String, otherwise unknown
   */
  default String version() {
    Pong pong = ping();
    return pong != null ? pong.getVersion() : "unknown";
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
  default CountInfo queryCountInfo(String measurement, String column, long startTime, long endTime) {
    return queryCountInfo(getDatabase(), measurement, column, startTime, endTime, null);
  }

  /**
   * 统计
   *
   * @param db          数据库
   * @param measurement 表
   * @param column      列
   * @param startTime   开始时间
   * @param endTime     结束时间
   * @return 返回统计信息
   */
  default CountInfo queryCountInfo(String db, String measurement, String column, long startTime, long endTime, String condition) {
    String utcPattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    String clause = String.format("FROM \"%s\" WHERE time >= '%s' AND time <= '%s' %s"
        , measurement, DateFmtter.fmtUtc(startTime, utcPattern), DateFmtter.fmtUtc(endTime, utcPattern), (condition != null ? condition.trim() : ""));
    // count
    String sql = String.format("SELECT count(%s) AS count %s", column, clause)
        // first
        + String.format(";\nSELECT first(%s) AS first %s ORDER BY time ASC LIMIT 1", column, clause)
        // last
        + String.format(";\nSELECT last(%s) AS last %s ORDER BY time DESC LIMIT 1", column, clause);
    final CountInfo info = new CountInfo();
    info.setSql(sql);
    query(db, sql, 100).subscribe(qr -> {
      if (qr.hasError()) {
        info.setError(qr.getError());
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
            if (column.equalsIgnoreCase("*")) {
              for (String columnName : c.getColumns()) {
                if (columnName.equalsIgnoreCase("time")) {
                  continue;
                }
                info.getDetails().put(columnName, c.getValue(columnName, null));
                if (columnName.startsWith("count")) {
                  info.setCount(Math.max(c.getLong(columnName, 0L), info.getCount()));
                } else if (columnName.startsWith("first")) {
                  if (c.getValue(columnName, null) != null)
                    info.setStartTime(c.getTime());
                } else if (columnName.startsWith("last")) {
                  if (c.getValue(columnName, null) != null)
                    info.setEndTime(c.getTime());
                }
              }
            } else {
              info.setCount(Math.max(c.getLong("count", 0L), info.getCount()));
              if (c.getValue("first", null) != null) info.setStartTime(c.getTime());
              if (c.getValue("last", null) != null) info.setEndTime(c.getTime());
            }
          });
    });
    return info;
  }

  /**
   * create new database
   *
   * @param database database name
   * @return result
   */
  default QueryResult createDatabase(String database) {
    return postQuery(Query.encode("CREATE DATABASE \"" + database + "\""));
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
        .filter(s -> InfluxUtils.isNotBlank(database) || s.getName().equals(database))
        .filter(s -> s.getValues() != null)
        .flatMap(s -> s.getValues()
            .stream()
            .map(values -> {
              ContinuousQuery cq = new ContinuousQuery();
              cq.setDatabase(s.getName());
              cq.setName(String.valueOf(values.get(0)));
              cq.setQuery(String.valueOf(values.get(1)));
              return cq;
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
    return getConverterFactory().mapperTo(queryResult, RetentionPolicy.class);
  }

  /**
   * 创建保留策略
   */
  default QueryResult createRetentionPolicy(final String rpName,
                                            final String database,
                                            final String duration,
                                            final String shardDuration,
                                            final int replicationFactor,
                                            final boolean isDefault) {
    Preconditions.checkNonEmptyString(rpName, "retentionPolicyName");
    Preconditions.checkNonEmptyString(database, "database");
    Preconditions.checkNonEmptyString(duration, "retentionDuration");
    Preconditions.checkDuration(duration, "retentionDuration");
    if (StringUtils.isNotBlank(shardDuration)) {
      Preconditions.checkDuration(shardDuration, "shardDuration");
    }
    Preconditions.checkPositiveNumber(replicationFactor, "replicationFactor");

    StringBuilder queryBuilder = new StringBuilder("CREATE RETENTION POLICY \"");
    queryBuilder.append(rpName)
        .append("\" ON \"")
        .append(database)
        .append("\" DURATION ")
        .append(duration)
        .append(" REPLICATION ")
        .append(replicationFactor);
    if (shardDuration != null && !shardDuration.isEmpty()) {
      queryBuilder.append(" SHARD DURATION ");
      queryBuilder.append(shardDuration);
    }
    if (isDefault) {
      queryBuilder.append(" DEFAULT");
    }
    return postQuery(Query.encode(queryBuilder.toString()));
  }

  /**
   * 创建保留策略
   */
  default QueryResult createRetentionPolicy(final String rpName,
                                            final String database,
                                            final String duration,
                                            final int replicationFactor,
                                            final boolean isDefault) {
    return createRetentionPolicy(rpName, database, duration, null, replicationFactor, isDefault);
  }

  /**
   * 创建保留策略
   */
  default QueryResult createRetentionPolicy(final String rpName,
                                            final String database,
                                            final String duration,
                                            final String shardDuration,
                                            final int replicationFactor) {
    return createRetentionPolicy(rpName, database, duration, shardDuration, replicationFactor, false);
  }

  /**
   * 创建订阅者(用于备份数据)
   *
   * @param name      名称
   * @param isAny     是否为 ANY，如果为ANY，有多个UDP订阅时会轮询分发
   * @param addresses 远程地址
   * @return 创建结果
   */
  default QueryResult createSubscription(String name, boolean isAny, String... addresses) {
    return createSubscription(name, getDatabase(), getRetentionPolicy(), isAny, addresses);
  }

  /**
   * 创建订阅者(用于备份数据)
   *
   * @param name            名称
   * @param db              数据库
   * @param retentionPolicy 保留策略
   * @param isAny           是否为 ANY，如果为ANY，有多个UDP订阅时会轮询分发
   * @param addresses       远程地址
   * @return 创建结果
   */
  default QueryResult createSubscription(String name, String db, String retentionPolicy, boolean isAny, String... addresses) {
    // CREATE SUBSCRIPTION "sub0" ON "mydb"."autogen" DESTINATIONS ALL 'http://www.example.com:8086', 'http://www.example2.com:8086'
    // CREATE SUBSCRIPTION "sub0" ON "mydb"."autogen" DESTINATIONS ANY 'udp://www.example.com:9090', 'udp://www.example2.com:9090'
    String query = String.format("CREATE SUBSCRIPTION \"%s\" ON \"%s\".\"%s\" DESTINATIONS %s '%s'"
        , name, db, retentionPolicy, isAny ? "ANY" : "ALL", String.join("', '", addresses));
    QueryResult result = postQuery(query);
    if (result.hasError()) {
      throw new IllegalStateException(result.getError());
    }
    return result;
  }

  /**
   * 展示当前正在订阅的配置
   */
  default List<Subscription> showSubscriptions() {
    List<Subscription> subscriptions = new ArrayList<>();
    QueryResult queryResult = postQuery("SHOW SUBSCRIPTIONS");
    Flowable.just(queryResult)
        .subscribe(new QueryObserver() {
          @Override
          public void onSeriesNext(List<Object> values, ValueConverter c, int position) {
            JSONObject json = new JSONObject();
            json.put("db", c.getName());
            c.getColumns().forEach(column -> json.put(column, c.getValue(column, null)));
            subscriptions.add(json.toJavaObject(Subscription.class));
          }
        });
    return subscriptions;
  }

  /**
   * 创建订阅者(用于备份数据)
   *
   * @param name 名称
   * @return 创建结果
   */
  default QueryResult dropSubscription(String name) {
    return dropSubscription(name, getDatabase(), getRetentionPolicy());
  }

  /**
   * 创建订阅者(用于备份数据)
   *
   * @param name            名称
   * @param db              数据库
   * @param retentionPolicy 保留策略
   * @return 创建结果
   */
  default QueryResult dropSubscription(String name, String db, String retentionPolicy) {
    // DROP SUBSCRIPTION "<subscription_name>" ON "<db_name>"."<retention_policy>"
    return postQuery(String.format("DROP SUBSCRIPTION \"%s\" ON \"%s\".\"%s\"", name, db, retentionPolicy));
  }

  /**
   * 创建 continuous query
   *
   * @param name          名称
   * @param database      数据库
   * @param selectIntoSQL select into 语句: select a,b,c from test where time >= now() - 1d into test2
   * @return 返回结果
   */
  default QueryResult createContinuousQuery(String name, String database, String selectIntoSQL) {
    return postQuery("CREATE CONTINUOUS QUERY \"" + name + "\" ON \"" + database + "\"\n"
        + "BEGIN\n"
        + selectIntoSQL
        + "END\n");
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
   * 删除TAG
   *
   * @param db          数据库
   * @param measurement 表
   * @param tagName     TAG名
   */
  @Deprecated
  default void deleteTagValues(String db, String measurement, String tagName) {
    //postQuery(db, "DELETE ");
    throw new IllegalStateException("不支持此操作");
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
   * 删除数据库
   *
   * @param measurement 表名
   * @return 返回结果
   */
  default QueryResult dropMeasurement(String measurement) {
    return dropMeasurement(getDatabase(), measurement);
  }

  /**
   * 删除数据库
   *
   * @param database    数据库名
   * @param measurement 表名
   * @return 返回结果
   */
  default QueryResult dropMeasurement(String database, String measurement) {
    return postQuery(database, "DROP MEASUREMENT " + measurement);
  }

  /**
   * 删除数据库
   *
   * @return 返回结果
   */
  default QueryResult dropDatabase() {
    return dropDatabase(getDatabase());
  }

  /**
   * 删除数据库
   *
   * @param database 数据库名
   * @return 返回结果
   */
  default QueryResult dropDatabase(String database) {
    return postQuery(database, "DROP DATABASE " + database);
  }

  /**
   * Obtain field key map
   *
   * @param retentionPolicy retention policy
   * @param measurement     measurement
   * @return return field key map
   */
  default Map<String, FieldKey> getFieldKeyMap(String retentionPolicy, String measurement) {
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
  default Map<String, FieldKey> getFieldKeyMap(String db, String retentionPolicy, String measurement) {
    return getFieldKeyMap(db, retentionPolicy, measurement, true);
  }

  /**
   * Obtain field key map
   *
   * @param measurement measurement
   * @param containTags contains tag
   * @return return field key map
   */
  default Map<String, FieldKey> getFieldKeyMap(String measurement, boolean containTags) {
    return getFieldKeyMap(getDatabase(), getRetentionPolicy(), measurement, containTags);
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
  default Map<String, FieldKey> getFieldKeyMap(String db, String retentionPolicy, String measurement, boolean containTags) {
    final String sql = "SHOW FIELD KEYS FROM \"" + retentionPolicy + "\".\"" + measurement + "\"";
    QueryResult queryResult = postQuery(db, sql);
    List<FieldKey> fieldKeys = getObjectsStream(queryResult)
        .flatMap(values -> {
          FieldKey ifk = new FieldKey.Builder()
              .setColumn((String) values.get(0))
              .setFieldType(FieldKey.getFieldType((String) values.get(1)))
              .build();
          return Stream.of(ifk);
        })
        .collect(Collectors.toList());

    if (containTags) {
      List<String> tagKeys = getTagKeys(db, measurement);
      fieldKeys.addAll(tagKeys.stream()
          .flatMap(tag -> {
            FieldKey FieldKey = new FieldKey.Builder()
                .setColumn(tag)
                .setFieldType(String.class)
                .setTag(true)
                .build();
            return Stream.of(FieldKey);
          })
          .collect(Collectors.toList()));
    }

    if (!fieldKeys.isEmpty()) {
      fieldKeys.add(new FieldKey.Builder()
          .setColumn("time")
          .setFieldType(String.class)
          .setTag(true)
          .setTimestamp(true)
          .build());
    }

    if (fieldKeys.isEmpty()) {
      return Collections.emptyMap();
    }
    final Map<String, FieldKey> fieldKeyMap = new ConcurrentHashMap<>();
    for (FieldKey FieldKey : fieldKeys) {
      fieldKeyMap.put(FieldKey.getColumn(), FieldKey);
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
    if (checkResult(result))
      return result.getResults();
    throw new IllegalStateException(result.getError());
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

  /**
   * 导出成文件
   *
   * @param out         输出的流
   * @param measurement 表名
   * @param chunkSize   块大小，即每次分批给多少条数据
   * @param startTime   开始时间
   * @param endTime     结束时间
   * @param condition   条件：AND deviceId = '123456' AND status = 1
   * @return 是否导出成功，如果不存在，导出则为false，否则为true，或抛出异常
   */
  default boolean export(File out, String measurement, int chunkSize, Long startTime, Long endTime, String condition) {
    try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8));) {
      return export(writer, measurement, chunkSize, startTime, endTime, condition);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 导出成文件
   *
   * @param out         输出的流
   * @param measurement 表名
   * @param chunkSize   块大小，即每次分批给多少条数据
   * @param startTime   开始时间
   * @param endTime     结束时间
   * @param condition   条件：AND deviceId = '123456' AND status = 1
   * @return 是否导出成功，如果不存在，导出则为false，否则为true，或抛出异常
   */
  default boolean export(Writer out, String measurement, int chunkSize, Long startTime, Long endTime, String condition) {
    return export(out, getDatabase(), getRetentionPolicy(), measurement, chunkSize, startTime, endTime, condition);
  }

  /**
   * 导出成文件
   *
   * @param out             输出的流
   * @param db              数据库
   * @param retentionPolicy 策略
   * @param measurement     表名
   * @param chunkSize       块大小，即每次分批给多少条数据
   * @param startTime       开始时间
   * @param endTime         结束时间
   * @param condition       条件：AND deviceId = '123456' AND status = 1
   * @return 是否导出成功，如果不存在，导出则为false，否则为true，或抛出异常
   */
  default boolean export(Writer out, String db, String retentionPolicy, String measurement, int chunkSize, Long startTime, Long endTime, String condition) {
    Map<String, FieldKey> fieldKeyMap = getFieldKeyMap(db, retentionPolicy, measurement, true);
    if (fieldKeyMap.isEmpty()) {
      return false;
    }
    String clause = (""
        + (startTime != null ? "time >= '" + DateFmtter.fmtUtcS(startTime) + "'" : "")
        + (endTime != null ? "AND time <= '" + DateFmtter.fmtUtcS(endTime) + "'" : "")
        + condition).trim();
    String sql = "SELECT * FROM \"" + measurement + "\" " + String.join(" ",
        clause.isEmpty() ? clause : (clause.startsWith("WHERE") || clause.startsWith("where") ? clause : "WHERE " + clause)
    );
    AtomicReference<Throwable> error = new AtomicReference<>();
    Disposable disposable = query(db, sql, chunkSize)
        .subscribe(queryResult -> {
          List<Point> points = InfluxUtils.toPoint(queryResult, fieldKeyMap);
          out.write(points.stream()
              .map(Point::lineProtocol)
              .collect(Collectors.joining("\n")));
          out.write("\n");
          out.flush();
        }, error::set);
    ShutdownHook.register(disposable::dispose);
    if (error.get() != null) {
      throw new IllegalStateException(error.get());
    }
    return true;
  }

}
