package com.benefitj.spring.influxdb.template;

import com.benefitj.spring.influxdb.InfluxException;
import com.benefitj.spring.influxdb.dto.QueryResult;

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
