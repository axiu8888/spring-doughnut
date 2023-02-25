package com.benefitj.influxdb.msgpack;

import com.benefitj.influxdb.dto.QueryResult;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;
import java.io.InputStream;

/**
 * Test the InfluxDB API over MessagePack format.
 *
 * @author hoan.le [at] bonitoo.io
 */
public class MessagePackResponseBodyConverter implements Converter<ResponseBody, QueryResult> {

  @Override
  public QueryResult convert(final ResponseBody value) throws IOException {
    try (InputStream is = value.byteStream()) {
      MessagePackTraverser traverser = new MessagePackTraverser();
      return traverser.parse(is);
    }
  }
}
