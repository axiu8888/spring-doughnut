package com.benefitj.spring.quartz;

import com.benefitj.frameworks.cglib.CGLibProxy;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 调度接口
 */
public interface IScheduler extends Scheduler {

  static IScheduler create(Scheduler scheduler) {
    return CGLibProxy.newProxy(null, IScheduler.class, scheduler);
  }

  @Override
  String getSchedulerName();

  @Override
  String getSchedulerInstanceId();

  @Override
  SchedulerContext getContext();

  @Override
  void start();

  @Override
  void startDelayed(int seconds);

  @Override
  boolean isStarted();

  @Override
  void standby();

  @Override
  boolean isInStandbyMode();

  @Override
  void shutdown();

  @Override
  void shutdown(boolean waitForJobsToComplete);

  @Override
  boolean isShutdown();

  @Override
  SchedulerMetaData getMetaData();

  @Override
  List<JobExecutionContext> getCurrentlyExecutingJobs();

  @Override
  void setJobFactory(JobFactory factory);

  @Override
  ListenerManager getListenerManager();

  @Override
  Date scheduleJob(JobDetail jobDetail, Trigger trigger);

  @Override
  Date scheduleJob(Trigger trigger);

  @Override
  void scheduleJobs(Map<JobDetail, Set<? extends Trigger>> triggersAndJobs, boolean replace);

  @Override
  void scheduleJob(JobDetail jobDetail, Set<? extends Trigger> triggersForJob, boolean replace);

  @Override
  boolean unscheduleJob(TriggerKey triggerKey);

  @Override
  boolean unscheduleJobs(List<TriggerKey> triggerKeys);

  @Override
  Date rescheduleJob(TriggerKey triggerKey, Trigger newTrigger);

  @Override
  void addJob(JobDetail jobDetail, boolean replace);

  @Override
  void addJob(JobDetail jobDetail, boolean replace, boolean storeNonDurableWhileAwaitingScheduling);

  @Override
  boolean deleteJob(JobKey jobKey);

  @Override
  boolean deleteJobs(List<JobKey> jobKeys);

  @Override
  void triggerJob(JobKey jobKey);

  @Override
  void triggerJob(JobKey jobKey, JobDataMap data);

  @Override
  void pauseJob(JobKey jobKey);

  @Override
  void pauseJobs(GroupMatcher<JobKey> matcher);

  @Override
  void pauseTrigger(TriggerKey triggerKey);

  @Override
  void pauseTriggers(GroupMatcher<TriggerKey> matcher);

  @Override
  void resumeJob(JobKey jobKey);

  @Override
  void resumeJobs(GroupMatcher<JobKey> matcher);

  @Override
  void resumeTrigger(TriggerKey triggerKey);

  @Override
  void resumeTriggers(GroupMatcher<TriggerKey> matcher);

  @Override
  void pauseAll();

  @Override
  void resumeAll();

  @Override
  List<String> getJobGroupNames();

  @Override
  Set<JobKey> getJobKeys(GroupMatcher<JobKey> matcher);

  @Override
  List<? extends Trigger> getTriggersOfJob(JobKey jobKey);

  @Override
  List<String> getTriggerGroupNames();

  @Override
  Set<TriggerKey> getTriggerKeys(GroupMatcher<TriggerKey> matcher);

  @Override
  Set<String> getPausedTriggerGroups();

  @Override
  JobDetail getJobDetail(JobKey jobKey);

  @Override
  Trigger getTrigger(TriggerKey triggerKey);

  @Override
  Trigger.TriggerState getTriggerState(TriggerKey triggerKey);

  @Override
  void resetTriggerFromErrorState(TriggerKey triggerKey);

  @Override
  void addCalendar(String calName, Calendar calendar, boolean replace, boolean updateTriggers);

  @Override
  boolean deleteCalendar(String calName);

  @Override
  Calendar getCalendar(String calName);

  @Override
  List<String> getCalendarNames();

  @Override
  boolean interrupt(JobKey jobKey);

  @Override
  boolean interrupt(String fireInstanceId);

  @Override
  boolean checkExists(JobKey jobKey);

  @Override
  boolean checkExists(TriggerKey triggerKey);

  @Override
  void clear();
}
