package com.benefitj.minio;

import io.minio.messages.RetentionDuration;
import io.minio.messages.RetentionDurationUnit;

public class DefaultRetentionDuration implements RetentionDuration {

  int duration;
  RetentionDurationUnit unit;

  public DefaultRetentionDuration(int duration, RetentionDurationUnit unit) {
    this.duration = duration;
    this.unit = unit;
  }

  @Override
  public RetentionDurationUnit unit() {
    return unit;
  }

  @Override
  public int duration() {
    return duration;
  }

}
