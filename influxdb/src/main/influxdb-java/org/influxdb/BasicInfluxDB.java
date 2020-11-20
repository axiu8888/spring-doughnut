package org.influxdb;

import okhttp3.RequestBody;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.io.File;

/**
 * Interface with all available methods to access a BasicInfluxDB database.
 */
public interface BasicInfluxDB extends InfluxDB {

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param batchPoints     the points in the correct lineprotocol.
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  void write(String database, String retentionPolicy, ConsistencyLevel consistency, File batchPoints);

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param batchPoints     the points in the correct lineprotocol.
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  void write(String database, String retentionPolicy, ConsistencyLevel consistency, RequestBody batchPoints);

  /**
   * Execute a query against a database.
   *
   * @param query     the query to execute.
   * @param chunkSize the number of QueryResults to process in one chunk.
   * @param consumer  the consumer to invoke when result is received
   */
  void queryString(Query query, int chunkSize, QueryObserver<String> consumer);

  /**
   * 执行 query / delete etc...
   *
   * @param query the query to execute.
   * @return a List of Series which matched the query.
   */
  QueryResult postQuery(String query);

  /**
   * execute query / delete etc...
   *
   * @param db    database name
   * @param query the query to execute.
   * @return a List of Series which matched the query.
   */
  QueryResult postQuery(String db, String query);

}
