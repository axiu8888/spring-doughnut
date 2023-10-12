package com.benefitj.spring.quartz;

import com.benefitj.core.IdUtils;
import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.quartz.worker.QuartzWorkerManager;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;

import java.text.ParseException;
import java.util.Date;

public class QuartzUtils {

  /**
   * 设置默认的参数
   *
   * @param job 调度任务
   * @return 返回调度任务
   */
  public static QuartzJob setup(QuartzJob job) {
    return setup(job, job.getTriggerType().name() + "_" + IdUtils.uuid(8));
  }

  /**
   * 设置默认的参数
   *
   * @param job    调度任务
   * @param jobName job 名称
   * @return 返回调度任务
   */
  public static QuartzJob setup(QuartzJob job, String jobName) {
    TriggerType triggerType = job.getTriggerType();
    if (triggerType == null) {
      throw new QuartzException("请指定正确的触发器类型");
    }

    checkWorker(job);

    // 触发器组名称
    job.setTriggerGroup(triggerType.name());
    // 创建随机的触发器名称
    job.setTriggerName("trigger-" + jobName);
    // Job组名称
    job.setJobGroup(triggerType.name());
    // 创建随机的 JobName
    job.setJobName(jobName);

    // 开始时间
    long now = System.currentTimeMillis();
    if (job.getStartAt() == null) {
      job.setStartAt(now);
    }

    // 调度的时间不能比当前时间更早
    if (job.getStartAt() < now) {
      job.setStartAt(now);
    }

    if (triggerType == TriggerType.CRON) {
      try {
        // 验证表达式
        CronExpression.validateExpression(job.getCronExpression());
      } catch (ParseException e) {
        throw new QuartzException("[" + job.getCronExpression() + "]表达式错误: " + e.getMessage());
      }
    } else {
      // 验证Simple的值
      // 执行次数
      if (job.getSimpleRepeatCount() == null) {
        job.setSimpleRepeatCount(0);
      }
      // 间隔时间
      if (job.getSimpleInterval() == null) {
        job.setSimpleInterval(0L);
      }
    }

    if (job.getEndAt() != null) {
      // 至少开始后的5秒再结束
      job.setEndAt(Math.max(job.getStartAt() + 5_000, job.getEndAt()));
    }

    // job类型
    JobType jobType = job.getJobType() != null ? job.getJobType() : JobType.DEFAULT;
    // 任务类型
    job.setJobType(jobType);
    return job;
  }

  /**
   * 检查 job 的 worker
   */
  public static void checkWorker(QuartzJob job) {
    WorkerType workerType = job.getWorkerType();
    if (workerType == null) {
      throw new QuartzException("WorkerType不能为空");
    }

    if (StringUtils.isBlank(job.getWorker())) {
      throw new QuartzException("worker不能为空");
    }

    switch (workerType) {
      case QUARTZ_WORKER:
        if (!QuartzWorkerManager.get().containsKey(job.getWorker())) {
          throw new QuartzException("无法发现对应的QuartzWorker: " + job.getWorker());
        }
        break;
      case NEW_INSTANCE:
        try {
          Class<?> cls = Class.forName(job.getWorker());
          if (!cls.isAssignableFrom(JobWorker.class)) {
            throw new QuartzException("请指定正确的 JobWorker 类型");
          }
        } catch (ClassNotFoundException e) {
          throw new QuartzException("请指定正确的worker");
        }
        break;
      case SPRING_BEAN_NAME:
        String worker = job.getWorker();
        if (!(SpringCtxHolder.containsBean(worker))
            || !(SpringCtxHolder.getBean(job.getWorker()) instanceof JobWorker)) {
          throw new QuartzException("未发现匹配的JobWorker实例!");
        }
        break;
      default:
    }
  }

  /**
   * 构建 JobDetail
   *
   * @param job 任务
   * @return 返回 JobBuilder
   */
  public static JobBuilder job(QuartzJob job) {
    checkWorker(job);
    // 创建 JobDetails
    JobBuilder jb = JobBuilder.newJob();
    jb.ofType(job.getJobType().getJobClass());
    jb.withIdentity(job.getJobName(), job.getJobGroup());
    jb.withDescription(job.getDescription());
    jb.requestRecovery(false);
    // 不持久化
    jb.storeDurably(false);
    jb.usingJobData(JobWorker.KEY_ID, job.getId());
    jb.usingJobData(JobWorker.KEY_JOB_DATA, job.getJobData());
    jb.usingJobData(JobWorker.KEY_JOB, JsonUtils.toJson(job));
    jb.usingJobData(JobWorker.KEY_JOB_CLASS, job.getClass().getName());
    return jb;
  }

  /**
   * 构建触发器
   *
   * @param job 任务
   * @return 返回TriggerBuilder
   */
  public static TriggerBuilder<Trigger> trigger(QuartzJob job) {
    TriggerType triggerType = job.getTriggerType();
    if (triggerType == null) {
      throw new QuartzException("触发器类型错误");
    }
    return triggerType == TriggerType.CRON ? cronTrigger(job) : simpleTrigger(job);
  }

  /**
   * 构建触发器
   *
   * @param job 任务
   * @return 返回TriggerBuilder
   */
  public static TriggerBuilder<Trigger> trigger(QuartzJob job, TriggerBuilder<Trigger> tb) {
    tb.withIdentity(job.getTriggerName(), job.getTriggerGroup());
    // 开始执行的时间
    if (job.getStartAt() != null) {
      tb.startAt(new Date(job.getStartAt()));
    } else {
      tb.startNow();
    }
    // 结束时间
    if (job.getEndAt() != null) {
      tb.endAt(new Date(job.getEndAt()));
    }
    return tb;
  }

  /**
   * 构建触发器
   *
   * @param job 任务
   * @return 返回TriggerBuilder
   */
  public static TriggerBuilder<Trigger> simpleTrigger(QuartzJob job) {
    TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger();
    trigger(job, tb);
    // 调度器
    SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();
    TriggerType.SimplePolicy.schedulePolicy(ssb, TriggerType.SimplePolicy.SMART_POLICY);
    ssb.withIntervalInMilliseconds(job.getSimpleInterval());
    ssb.withRepeatCount(job.getSimpleRepeatCount());
    tb.withSchedule(ssb);
    return tb;
  }

  /**
   * 构建触发器
   *
   * @param job 任务
   * @return 返回TriggerBuilder
   */
  public static TriggerBuilder<Trigger> cronTrigger(QuartzJob job) {
    TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger();
    trigger(job, tb);
    // 调度器
    CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule(job.getCronExpression());
    TriggerType.CronPolicy.schedulePolicy(csb, TriggerType.CronPolicy.DO_NOTHING);
    tb.withSchedule(csb);
    return tb;
  }

  /**
   * 触发器的Key
   */
  public static JobKey jobKey(QuartzJob job) {
    return new JobKey(job.getJobName(), job.getJobGroup());
  }

  /**
   * 触发器的Key
   */
  public static TriggerKey triggerKey(QuartzJob job) {
    return new TriggerKey(job.getTriggerName(), job.getTriggerGroup());
  }

  public static void scheduleJob(Scheduler scheduler, QuartzJob job) throws IllegalStateException {
    try {
      // 设置默认值
      setup(job);

      // 创建 JobDetails
      if (scheduler.checkExists(jobKey(job))) {
        scheduler.resumeJob(jobKey(job));
        return;
      }

      JobDetail jd = job(job).build();

      Trigger trigger = scheduler.getTrigger(triggerKey(job));
      if (trigger == null) {
        trigger = trigger(job).forJob(jd).build();
      }

      // 添加调度器
      scheduler.scheduleJob(jd, trigger);

      // 是否暂停job
      if (!Boolean.TRUE.equals(job.getActive())) {
        scheduler.pauseJob(jd.getKey());
      }

      // 开启调度器
      if (!scheduler.isStarted()) {
        scheduler.start();
      }
    } catch (SchedulerException e) {
      throw new IllegalStateException(e);
    }
  }
}
