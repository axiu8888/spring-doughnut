package com.benefitj.scaffold.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public interface SimpleJobListener extends JobListener {

  @Override
  default String getName() {
    return getClass().getName() + "#" + Integer.toHexString(hashCode());
  }

  @Override
  default void jobToBeExecuted(JobExecutionContext context) {
    // 被执行之前
  }

  @Override
  default void jobExecutionVetoed(JobExecutionContext context) {
    // 触发器优先级排序
  }

  @Override
  default void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
    // 被执行
  }
}
