package com.benefitj.spring.influxdb.template;

import com.benefitj.core.log.ILogger;
import com.benefitj.spring.influxdb.InfluxApi;
import com.benefitj.spring.influxdb.InfluxDBLogger;
import com.benefitj.spring.influxdb.InfluxException;
import com.benefitj.spring.influxdb.InfluxOptions;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.QueryResult;
import com.benefitj.spring.influxdb.msgpack.MessagePackTraverser;
import com.squareup.moshi.JsonAdapter;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Response;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * InfluxDB 模板
 *
 * @author DINGXIUAN
 */
public class InfluxTemplateImpl implements InfluxTemplate {

  final ILogger log = InfluxDBLogger.get();

  private InfluxOptions options;
  private InfluxApi api;
  private PointConverterFactory converterFactory = PointConverterFactory.get();
  private JsonAdapter<QueryResult> jsonAdapter;

  public InfluxTemplateImpl() {
  }

  @Override
  public InfluxApi getApi() {
    return api;
  }

  public void setApi(InfluxApi api) {
    this.api = api;
  }

  @Override
  public InfluxOptions getOptions() {
    return options;
  }

  public void setOptions(InfluxOptions options) {
    this.options = options;
  }

  @Override
  public PointConverterFactory getConverterFactory() {
    return converterFactory;
  }

  public void setConverterFactory(PointConverterFactory converterFactory) {
    this.converterFactory = converterFactory;
  }

  public JsonAdapter<QueryResult> getJsonAdapter() {
    return jsonAdapter;
  }

  public void setJsonAdapter(JsonAdapter<QueryResult> jsonAdapter) {
    this.jsonAdapter = jsonAdapter;
  }

  @Override
  public Flowable<QueryResult> query(String db, String query, int chunkSize, String params) {
      return params == null
          ? execute(getApi().query(db, query, chunkSize))
          : execute(getApi().query(db, query, chunkSize, params));
  }

  @Override
  public QueryResult postQuery(String query) {
    log.debug("postQuery, query: {}", query);
    return execute(getApi().postQuery(query), r -> r);
  }

  @Override
  public QueryResult postQuery(String db, String query) {
    log.debug("postQuery, db: {}, query: {}", db, query);
    return execute(getApi().postQuery(db, query), r -> r);
  }

  protected Flowable<QueryResult> execute(Flowable<Response<ResponseBody>> query) {
    return query.flatMap(response -> Flowable.create(emitter -> {
      if (getOptions().getResponseFormat() == InfluxApi.ResponseFormat.MSGPACK) {
        adaptMsgPack(response, emitter);
      } else {
        adaptJson(response, emitter);
      }
    }, BackpressureStrategy.BUFFER));
  }

  protected void adaptJson(Response<ResponseBody> response, FlowableEmitter<QueryResult> emitter) throws IOException {
    if (response.isSuccessful()) {
      JsonAdapter<QueryResult> adapter = getJsonAdapter();
      try (final ResponseBody chunkedBody = response.body();
           final BufferedSource source = chunkedBody.source();) {
        for (;;) {
          QueryResult result = adapter.fromJson(source);
          if (result != null) {
            emitter.onNext(result);
          }
        }
      } catch (EOFException ignored) {
      } catch (IOException e) {
        emitter.tryOnError(e);
      } finally {
        emitter.onComplete();
      }
    } else {
      // REVIEW: must be handled consistently with IOException.
      ResponseBody errorBody = response.errorBody();
      if (errorBody != null) {
        emitter.tryOnError(new InfluxException(errorBody.string()));
      }
      emitter.onComplete();
    }
  }

  protected void adaptMsgPack(Response<ResponseBody> response, FlowableEmitter<QueryResult> emitter) throws IOException {
    try {
      if (response.isSuccessful()) {
        MessagePackTraverser traverser = new MessagePackTraverser();
        try (final ResponseBody chunkedBody = response.body();
             final InputStream is = chunkedBody.byteStream();) {
          for (QueryResult result : traverser.traverse(is)) {
            emitter.onNext(result);
          }
        } catch (IOException e) {
          emitter.tryOnError(e);
        }
      } else {
        // REVIEW: must be handled consistently with IOException.
        ResponseBody errorBody = response.errorBody();
        if (errorBody != null) {
          emitter.tryOnError(new InfluxException(errorBody.string()));
        }
      }
    } finally {
      emitter.onComplete();
    }
  }

  protected <V> V execute(Flowable<QueryResult> o, Function<QueryResult, V> mapped) {
    AtomicReference<V> ref = new AtomicReference<>();
    o.subscribe(SimpleSubscriber.create(
        result -> ref.set(mapped.apply(result))
        , e -> {
          QueryResult qr = new QueryResult();
          qr.setError(e.getMessage());
          ref.set(mapped.apply(qr));
        }));
    return ref.get();
  }

}
