package com.benefitj.influxdb.template;

import com.benefitj.influxdb.InfluxException;
import com.benefitj.influxdb.dto.QueryResult;

public abstract class QuerySubscriber extends SimpleSubscriber<QueryResult> {

  @Override
  public void onNext(QueryResult qr) {
    if (qr.hasError()) {
      onError(new InfluxException(qr.getError()));
    } else {
      onNext0(qr);
    }
  }

  public abstract void onNext0(QueryResult queryResult);

}
