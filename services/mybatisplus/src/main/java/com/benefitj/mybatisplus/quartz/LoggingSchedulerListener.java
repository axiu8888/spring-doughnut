package com.benefitj.mybatisplus.quartz;

import com.benefitj.core.DateFmtter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingSchedulerListener implements SchedulerListener {

  @Override
  public void jobScheduled(Trigger trigger) {
    // job调度
    print("jobScheduled, trigger ==:> key: {}, jobKey: {}, startTime: {}, endTime: {}"
        , trigger.getKey(), trigger.getJobKey()
        , fmtS(trigger.getStartTime())
        , fmtS(trigger.getEndTime()));
  }

  @Override
  public void jobUnscheduled(TriggerKey triggerKey) {
    // job取消调度
    print("jobUnscheduled, triggerKey: {}", triggerKey);
  }

  @Override
  public void triggerFinalized(Trigger trigger) {
    // 触发器结束
    print("triggerFinalized, triggerKey: {}", trigger.getKey());
  }

  @Override
  public void triggerPaused(TriggerKey triggerKey) {
    // 触发器暂停
    print("triggerPaused, triggerKey: {}", triggerKey);
  }

  @Override
  public void triggersPaused(String triggerGroup) {
    // 整个触发器组的触发器暂停
    print("triggersPaused, triggerGroup: {}", triggerGroup);
  }

  @Override
  public void triggerResumed(TriggerKey triggerKey) {
    // 恢复触发器
    print("triggerResumed, triggerKey: {}", triggerKey);
  }

  @Override
  public void triggersResumed(String triggerGroup) {
    // 恢复整个触发器组的触发器
    print("triggersResumed, triggerGroup: {}", triggerGroup);
  }

  @Override
  public void jobAdded(JobDetail jobDetail) {
    // job被添加
    print("jobAdded, jobDetailKey: {}", jobDetail.getKey());
  }

  @Override
  public void jobDeleted(JobKey jobKey) {
    // job被删除
    print("jobDeleted: {}", jobKey);
  }

  @Override
  public void jobPaused(JobKey jobKey) {
    // job被暂停
    print("jobPaused: {}", jobKey);
  }

  @Override
  public void jobsPaused(String jobGroup) {
    // job组被暂停
    print("jobsPaused: {}", jobGroup);
  }

  @Override
  public void jobResumed(JobKey jobKey) {
    // job被恢复
    print("jobResumed: {}", jobKey);
  }

  @Override
  public void jobsResumed(String jobGroup) {
    // job组被恢复
    print("jobsResumed: {}", jobGroup);
  }

  @Override
  public void schedulerError(String msg, SchedulerException cause) {
    // 调度错误
    print("schedulerError, msg: {}", msg);
  }

  @Override
  public void schedulerInStandbyMode() {
    // 待机模式
    print("schedulerInStandbyMode: {}", Integer.toHexString(hashCode()));
  }

  @Override
  public void schedulerStarted() {
    print("schedulerStarted, {}", Integer.toHexString(hashCode()));
  }

  @Override
  public void schedulerStarting() {
    print("schedulerStarting, {}", Integer.toHexString(hashCode()));
  }

  @Override
  public void schedulerShutdown() {
    print("schedulerShutdown");
  }

  @Override
  public void schedulerShuttingdown() {
    print("schedulerShuttingdown");
  }

  @Override
  public void schedulingDataCleared() {
    print("schedulingDataCleared");
  }

  public String fmtS(Object time) {
    return time != null ? DateFmtter.fmtS(time) : null;
  }

  public void print(String msg, Object... args) {
    log.info(msg, args);
  }
}
