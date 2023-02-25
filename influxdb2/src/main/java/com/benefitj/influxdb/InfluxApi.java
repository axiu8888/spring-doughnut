package com.benefitj.influxdb;

import com.benefitj.influxdb.dto.QueryResult;
import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.*;

public interface InfluxApi {

  String U = "u";
  String P = "p";
  String Q = "q";
  String DB = "db";
  String RP = "rp";
  String PARAMS = "params";
  String PRECISION = "precision";
  String CONSISTENCY = "consistency";
  String EPOCH = "epoch";
  String CHUNK_SIZE = "chunk_size";

  @GET("ping")
  Flowable<Response<ResponseBody>> ping();

  /**
   * @param database        db: required The database to write points
   * @param retentionPolicy rp: optional The retention policy to write points.
   *                        If not specified, the autogen retention
   * @param precision       optional The precision of the time stamps (n, u, ms, s, m, h).
   *                        If not specified, n
   * @param consistency     optional The write consistency level required for the write to succeed.
   *                        Can be one of one, any, all, quorum. Defaults to all.
   */
  @POST("write")
  Flowable<Response<ResponseBody>> writePoints(@Query(DB) String database,
                                               @Query(RP) String retentionPolicy,
                                               @Query(PRECISION) String precision,
                                               @Query(CONSISTENCY) String consistency,
                                               @Body RequestBody batchPoints);

  @GET("query")
  Flowable<QueryResult> query(@Query(DB) String db,
                              @Query(EPOCH) String epoch,
                              @Query(value = Q, encoded = true) String query);

  @POST("query")
  @FormUrlEncoded
  Flowable<QueryResult> query(@Query(DB) String db,
                              @Query(EPOCH) String epoch,
                              @Field(value = Q, encoded = true) String query,
                              @Query(value = PARAMS, encoded = true) String params);

  @GET("query")
  Flowable<QueryResult> query(@Query(DB) String db,
                              @Query(value = Q, encoded = true) String query);

  @POST("query")
  @FormUrlEncoded
  Flowable<QueryResult> postQuery(@Query(DB) String db,
                                  @Field(value = Q, encoded = true) String query);

  @POST("query")
  @FormUrlEncoded
  Flowable<QueryResult> postQuery(@Query(DB) String db,
                                  @Field(value = Q, encoded = true) String query,
                                  @Query(value = PARAMS, encoded = true) String params);

  @POST("query")
  @FormUrlEncoded
  Flowable<QueryResult> postQuery(@Field(value = Q, encoded = true) String query);

  @Streaming
  @GET("query?chunked=true")
  Flowable<Response<ResponseBody>> query(@Query(DB) String db,
                                         @Query(value = Q, encoded = true) String query,
                                         @Query(CHUNK_SIZE) int chunkSize);

  @Streaming
  @POST("query?chunked=true")
  @FormUrlEncoded
  Flowable<Response<ResponseBody>> query(@Query(DB) String db,
                                         @Field(value = Q, encoded = true) String query,
                                         @Query(CHUNK_SIZE) int chunkSize,
                                         @Query(value = PARAMS, encoded = true) String params);

  /**
   * ConsistencyLevel for write Operations.
   */
  enum ConsistencyLevel {
    /**
     * Write succeeds only if write reached all cluster members.
     */
    ALL("all"),
    /**
     * Write succeeds if write reached any cluster members.
     */
    ANY("any"),
    /**
     * Write succeeds if write reached at least one cluster members.
     */
    ONE("one"),
    /**
     * Write succeeds only if write reached a quorum of cluster members.
     */
    QUORUM("quorum");
    private final String value;

    private ConsistencyLevel(final String value) {
      this.value = value;
    }

    /**
     * Get the String value of the ConsistencyLevel.
     *
     * @return the lowercase String.
     */
    public String value() {
      return this.value;
    }
  }

  /**
   * Format of HTTP Response body from InfluxDB server.
   */
  public enum ResponseFormat {
    /**
     * application/json format.
     */
    JSON,
    /**
     * application/x-msgpack format.
     */
    MSGPACK
  }

}
