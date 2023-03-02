package com.benefitj.spring.influxdb;

import com.benefitj.http.ApiBuilder;
import com.benefitj.spring.influxdb.msgpack.MessagePackConverterFactory;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.io.IOException;
import java.time.Duration;

public interface InfluxApiFactory {

  /**
   * 创建
   *
   * @param options 参数
   * @return 返回创建InfluxDB对象
   */
  InfluxApi create(InfluxOptions options);


  //String APPLICATION_MSGPACK = "application/x-msgpack";
  //MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain");
  //String SHOW_DATABASE_COMMAND_ENCODED = com.benefitj.influxdb.dto.Query.encode("SHOW DATABASES");

  static InfluxApiFactory newInstance() {
    return new InfluxApiFactoryImpl();
  }

  public class InfluxApiFactoryImpl implements InfluxApiFactory {

    /**
     * 创建InfluxDB对象
     *
     * @param options 参数
     * @return 返回 InfluxDB 实例
     */
    @Override
    public InfluxApi create(InfluxOptions options) {
      OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
          .connectTimeout(Duration.ofSeconds(options.getConnectTimeout()))
          .readTimeout(Duration.ofSeconds(options.getReadTimeout()))
          .writeTimeout(Duration.ofSeconds(options.getWriteTimeout()));
      if (InfluxUtils.isNotBlank(options.getUsername()) && InfluxUtils.isNotBlank(options.getPassword())) {
        clientBuilder.addInterceptor(new BasicAuthInterceptor(options.getUsername(), options.getPassword()));
      }
      clientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(options.getLogLevel()));
      clientBuilder.addInterceptor(new GzipRequestInterceptor(options.isGzip()));

      Converter.Factory factory;
      if (InfluxApi.ResponseFormat.MSGPACK.equals(options.getResponseFormat())) {
        clientBuilder.addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
            .addHeader("Accept", "application/x-msgpack")
            .build()));
        factory = MessagePackConverterFactory.create();
      } else {
        factory = MoshiConverterFactory.create();
      }
      return ApiBuilder.newBuilder(InfluxApi.class)
          .setUseDefault(false)
          .setBaseUrl(options.getUrl())
          .setOkHttpClient(clientBuilder.build())
          .setLogLevel(HttpLoggingInterceptor.Level.NONE)
          .setGzipEnable(false)
          .addConverterFactories(factory)
          .addCallAdapterFactoryIfAbsent(RxJava2CallAdapterFactory.create())
          .addNetworkInterceptors()
          .build();
    }

  }


  class BasicAuthInterceptor implements Interceptor {

    private String credentials;

    public BasicAuthInterceptor(final String user, final String password) {
      credentials = Credentials.basic(user, password);
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
      Request request = chain.request();
      Request authenticatedRequest = request.newBuilder().header("Authorization", credentials).build();
      return chain.proceed(authenticatedRequest);
    }
  }

}
