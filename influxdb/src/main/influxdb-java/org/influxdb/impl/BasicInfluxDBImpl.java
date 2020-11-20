package org.influxdb.impl;

import com.benefitj.spring.influxdb.ReflectUtils;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.influxdb.BasicInfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.InfluxDBIOException;
import org.influxdb.QueryObserver;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.msgpack.MessagePackTraverser;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * basic InfluxDB implementation
 */
public class BasicInfluxDBImpl extends InfluxDBImpl implements BasicInfluxDB {

  private volatile boolean initialized = false;

  private InfluxDBService service;
  private boolean messagePack = false;
  private Boolean messagePackSupport;
  private ChunkProcessor<QueryResult> chunkProcessor;
  private StringChunkProcessor stringChunkProcessor;

  public BasicInfluxDBImpl(String url, String username, String password,
                           OkHttpClient.Builder okHttpBuilder, ResponseFormat responseFormat) {
    super(url, username, password, okHttpBuilder, responseFormat);
  }

  public BasicInfluxDBImpl(String url, String username, String password, OkHttpClient.Builder okHttpBuilder,
                           Retrofit.Builder retrofitBuilder, ResponseFormat responseFormat) {
    super(url, username, password, okHttpBuilder, retrofitBuilder, responseFormat);
  }

  public BasicInfluxDBImpl(String url, String username, String password, OkHttpClient.Builder client) {
    super(url, username, password, client);
  }

  public BasicInfluxDBImpl(String url, String username, String password, OkHttpClient.Builder client,
                           InfluxDBService service, JsonAdapter<QueryResult> adapter) {
    super(url, username, password, client, service, adapter);
  }

  public BasicInfluxDBImpl(String url, String username, String password, OkHttpClient.Builder client,
                           String database, String retentionPolicy, ConsistencyLevel consistency) {
    super(url, username, password, client, database, retentionPolicy, consistency);
  }

  public InfluxDBService getService() {
    if (!initialized) {
      synchronized (this) {
        initialized();
      }
    }
    return this.service;
  }

  private synchronized void initialized() {
    this.service = getFieldValue("influxDBService", InfluxDBService.class);
    if (this.service == null) {
      throw new IllegalStateException("Cannot obtain the InfluxDBService of instance");
    }

    this.messagePack = getFieldValue("messagePack", boolean.class);
    this.messagePackSupport = getFieldValue("messagePackSupport", Boolean.class);

    if (this.messagePackSupport == null) {
      this.messagePackSupport = checkMessagePackSupport();
    }

    if (this.messagePack) {
      this.chunkProcessor = new MessagePackChunkProcessor();
    } else {
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<QueryResult> adapter = moshi.adapter(QueryResult.class);
      this.chunkProcessor = new JsonChunkProcessor(adapter);
    }

    Moshi msgPackMoshi = new Moshi.Builder().build();
    JsonAdapter<QueryResult> adapter = msgPackMoshi.adapter(QueryResult.class);
    this.stringChunkProcessor = new StringChunkProcessor(adapter);

    this.initialized = true;
  }

  protected <T> T getFieldValue(String name, Class<T> requiredType) {
    Field field = ReflectUtils.getField(InfluxDBImpl.class, name);
    if (field == null) {
      Class<?> currentClass = getClass();
      final AtomicReference<Field> fieldRef = new AtomicReference<>();
      ReflectUtils.foreachField(InfluxDBImpl.class
          , f -> f.getType().isAssignableFrom(requiredType) && f.getDeclaringClass() != currentClass
          , fieldRef::set
          , f -> fieldRef.get() == null);
      field = fieldRef.get();
    }
    return ReflectUtils.getFieldValue(field, this, null);
  }

  public ChunkProcessor<QueryResult> getChunkProcessor() {
    return chunkProcessor;
  }

  public StringChunkProcessor getStringChunkProcessor() {
    return this.stringChunkProcessor;
  }

  @Override
  public void query(final Query query, final int chunkSize, final BiConsumer<Cancellable, QueryResult> onNext,
                    final Runnable onComplete, final Consumer<Throwable> onFailure) {
    Call<ResponseBody> call = callResponseBody(query, chunkSize);
    Cancellable cancellable = new InfluxDBCancellable(call);
    try {
      Response<ResponseBody> response = call.execute();
      if (response.isSuccessful()) {
        try {
          getChunkProcessor().process(response.body(), cancellable, onNext, onComplete);
        } catch (Throwable e) {
          onFailure.accept(e);
        }
      } else {
        // REVIEW: must be handled consistently with IOException.
        ResponseBody errorBody = response.errorBody();
        if (errorBody != null) {
          InfluxDBException influxDBException = new InfluxDBException(errorBody.string());
          if (onFailure == null) {
            throw influxDBException;
          } else {
            onFailure.accept(influxDBException);
          }
        }
      }
    } catch (IOException e) {
      QueryResult queryResult = new QueryResult();
      queryResult.setError(e.toString());
      onNext.accept(cancellable, queryResult);
      //passing null onFailure consumer is here for backward compatibility
      //where the empty queryResult containing error is propagating into onNext consumer
      if (onFailure != null) {
        onFailure.accept(e);
      }
    }
  }

  /**
   * Calls the influxDBService for the query.
   */
  protected Call<ResponseBody> callResponseBody(final Query query, int chunkSize) {
    Call<ResponseBody> call;
    String command = query.getCommandWithUrlEncoded();
    String database = query.getDatabase();
    if (query instanceof BoundParameterQuery) {
      BoundParameterQuery bpq = (BoundParameterQuery) query;
      call = getService().query(database, command, chunkSize,
          bpq.getParameterJsonWithUrlEncoded());
    } else {
      call = this.getService().query(database, command, chunkSize);
    }
    return call;
  }

  /**
   * Calls the influxDBService for the query.
   */
  protected Call<QueryResult> callQuery(final Query query) {
    Call<QueryResult> call;
    if (query instanceof BoundParameterQuery) {
      BoundParameterQuery boundParameterQuery = (BoundParameterQuery) query;
      call = this.getService().postQuery(query.getDatabase(), query.getCommandWithUrlEncoded(),
          boundParameterQuery.getParameterJsonWithUrlEncoded());
    } else {
      if (query.requiresPost()) {
        call = this.getService().postQuery(query.getDatabase(), query.getCommandWithUrlEncoded());
      } else {
        call = this.getService().query(query.getDatabase(), query.getCommandWithUrlEncoded());
      }
    }
    return call;
  }

  protected boolean checkMessagePackSupport() {
    Matcher matcher = Pattern.compile("(\\d+\\.*)+").matcher(version());
    if (!matcher.find()) {
      return false;
    }
    String s = matcher.group();
    String[] versionNumbers = s.split("\\.");
    final int major = Integer.parseInt(versionNumbers[0]);
    final int minor = Integer.parseInt(versionNumbers[1]);
    final int fromMinor = 4;
    return (major >= 2) || ((major == 1) && (minor >= fromMinor));
  }

  protected QueryResult executeQuery(final Call<QueryResult> call) {
    if (messagePack) {
      if (messagePackSupport == null) {
        messagePackSupport = checkMessagePackSupport();
      }

      if (!messagePackSupport) {
        throw new UnsupportedOperationException(
            "MessagePack format is only supported from InfluxDB version 1.4 and later");
      }
    }
    return execute(call);
  }

  protected <T> T execute(final Call<T> call) {
    try {
      Response<T> response = call.execute();
      if (response.isSuccessful()) {
        return response.body();
      }
      try (ResponseBody errorBody = response.errorBody()) {
        if (messagePack) {
          throw InfluxDBException.buildExceptionForErrorState(errorBody.byteStream());
        } else {
          throw InfluxDBException.buildExceptionForErrorState(errorBody.string());
        }
      }
    } catch (IOException e) {
      throw new InfluxDBIOException(e);
    }
  }

  @Override
  public void write(String database, String retentionPolicy, ConsistencyLevel consistency, File batchPoints) {
    write(database, retentionPolicy, consistency, RequestBody.create(MEDIA_TYPE_STRING, batchPoints));
  }

  @Override
  public void write(String database, String retentionPolicy, ConsistencyLevel consistency, RequestBody batchPoints) {
    execute(getService()
        .writePoints(database, retentionPolicy, null, consistency.value(), batchPoints));
  }

  /**
   * Execute a query against a database.
   *
   * @param query     the query to execute.
   * @param chunkSize the number of QueryResults to process in one chunk.
   * @param consumer  发射器
   * @return a List of Series which matched the query.
   */
  @Override
  public void queryString(Query query, int chunkSize, QueryObserver<String> consumer) {
    try {
      Call<ResponseBody> call = callResponseBody(query, chunkSize);
      Response<ResponseBody> response = call.execute();
      if (response.isSuccessful()) {
        ResponseBody chunkedBody = response.body();
        InfluxDBCancellable cancellable = new InfluxDBCancellable(call);
        getStringChunkProcessor().process(chunkedBody, cancellable, (cancellable1, result) -> consumer.onNext(result), consumer::onComplete);
      } else {
        // REVIEW: must be handled consistently with IOException.
        ResponseBody errorBody = response.errorBody();
        if (errorBody != null) {
          consumer.onError(new InfluxDBException(errorBody.string()));
        }
      }
    } catch (IOException e) {
      QueryResult queryResult = new QueryResult();
      queryResult.setError(e.toString());
      consumer.onNext(getStringChunkProcessor().toJson(queryResult));
      //passing null onFailure consumer is here for backward compatibility
      //where the empty queryResult containing error is propagating into onNext consumer
      consumer.onError(e);
    }
  }

  /**
   * 执行 query / delete etc...
   *
   * @param query the query to execute.
   * @return a List of Series which matched the query.
   */
  @Override
  public QueryResult postQuery(String query) {
    Call<QueryResult> call = getService().postQuery(query);
    return executeQuery(call);
  }

  /**
   * 执行 query / delete etc...
   *
   * @param db    database name
   * @param query the query to execute.
   * @return a List of Series which matched the query.
   */
  @Override
  public QueryResult postQuery(String db, String query) {
    Call<QueryResult> call = getService().postQuery(db, query);
    return executeQuery(call);
  }


  public interface ChunkProcessor<T> {
    void process(ResponseBody chunkedBody, Cancellable cancellable,
                 BiConsumer<Cancellable, T> consumer, Runnable onComplete) throws IOException;
  }

  public static class MessagePackChunkProcessor implements ChunkProcessor<QueryResult> {
    @Override
    public void process(final ResponseBody chunkedBody, final Cancellable cancellable,
                        final BiConsumer<Cancellable, QueryResult> consumer, final Runnable onComplete)
        throws IOException {
      MessagePackTraverser traverser = new MessagePackTraverser();
      try (InputStream is = chunkedBody.byteStream()) {
        for (Iterator<QueryResult> it = traverser.traverse(is).iterator(); it.hasNext() && !cancellable.isCanceled(); ) {
          QueryResult result = it.next();
          consumer.accept(cancellable, result);
        }
      }
      if (!cancellable.isCanceled()) {
        onComplete.run();
      }
    }
  }

  public static class JsonChunkProcessor implements ChunkProcessor<QueryResult> {
    private JsonAdapter<QueryResult> adapter;

    public JsonChunkProcessor(final JsonAdapter<QueryResult> adapter) {
      this.adapter = adapter;
    }

    @Override
    public void process(final ResponseBody chunkedBody, final Cancellable cancellable,
                        final BiConsumer<Cancellable, QueryResult> consumer, final Runnable onComplete)
        throws IOException {
      try {
        BufferedSource source = chunkedBody.source();
        while (!cancellable.isCanceled()) {
          QueryResult result = adapter.fromJson(source);
          if (result != null) {
            consumer.accept(cancellable, result);
          }
        }
      } catch (EOFException e) {
        QueryResult queryResult = new QueryResult();
        queryResult.setError("DONE");
        consumer.accept(cancellable, queryResult);
        if (!cancellable.isCanceled()) {
          onComplete.run();
        }
      } finally {
        chunkedBody.close();
      }
    }
  }

  public static class StringChunkProcessor implements ChunkProcessor<String> {

    private JsonAdapter<QueryResult> adapter;

    public StringChunkProcessor(final JsonAdapter<QueryResult> adapter) {
      this.adapter = adapter;
    }

    @Override
    public void process(final ResponseBody chunkedBody, final Cancellable cancellable,
                        final BiConsumer<Cancellable, String> consumer, final Runnable onComplete)
        throws IOException {
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(chunkedBody.byteStream()))) {
        String line;
        while (!cancellable.isCanceled() && (line = bufferedReader.readLine()) != null) {
          consumer.accept(cancellable, line);
        }
        onComplete.run();
      } catch (EOFException e) {
        QueryResult queryResult = new QueryResult();
        queryResult.setError("DONE");
        consumer.accept(cancellable, adapter.toJson(queryResult));
        if (!cancellable.isCanceled()) {
          onComplete.run();
        }
      } finally {
        chunkedBody.close();
      }
    }

    public String toJson(QueryResult queryResult) {
      return adapter.toJson(queryResult);
    }
  }

  public static class InfluxDBCancellable implements Cancellable {

    private final Call call;

    public InfluxDBCancellable(Call call) {
      this.call = call;
    }

    @Override
    public void cancel() {
      call.cancel();
    }

    @Override
    public boolean isCanceled() {
      return call.isCanceled();
    }
  }


}
