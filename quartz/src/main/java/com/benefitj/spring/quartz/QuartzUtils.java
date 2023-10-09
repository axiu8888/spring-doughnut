package com.benefitj.spring.quartz;

import com.benefitj.spring.ctx.SpringCtxHolder;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.context.ApplicationContext;

import java.text.ParseException;
import java.util.Date;

public class QuartzUtils {

  /**
   * 设置默认的参数
   *
   * @param task    调度任务
   * @param jobName job 名称
   * @return 返回调度任务
   */
  public static QuartzJobTask setup(QuartzJobTask task, String jobName) {
    TriggerType triggerType = task.getTriggerType();
    if (triggerType == null) {
      throw new QuartzException("请指定正确的触发器类型");
    }

    checkWorker(task);

    // 触发器组名称
    task.setTriggerGroup(triggerType.name());
    // 创建随机的触发器名称
    task.setTriggerName("trigger-" + jobName);
    // Job组名称
    task.setJobGroup(triggerType.name());
    // 创建随机的 JobName
    task.setJobName(jobName);

    // 开始时间
    long now = System.currentTimeMillis();
    if (task.getStartAt() == null) {
      task.setStartAt(now);
    }

    // 调度的时间不能比当前时间更早
    if (task.getStartAt() < now) {
      task.setStartAt(now);
    }

    if (triggerType == TriggerType.CRON) {
      try {
        // 验证表达式
        CronExpression.validateExpression(task.getCronExpression());
      } catch (ParseException e) {
        throw new QuartzException("[" + task.getCronExpression() + "]表达式错误: " + e.getMessage());
      }
      if (task.getMisfirePolicy() == null) {
        // 默认什么都不做
        task.setMisfirePolicy(TriggerType.CronPolicy.DO_NOTHING.getPolicy());
      }
    } else {
      // 验证Simple的值
      // 执行次数
      if (task.getSimpleRepeatCount() == null) {
        task.setSimpleRepeatCount(0);
      }
      // 间隔时间
      if (task.getSimpleInterval() == null) {
        task.setSimpleInterval(0L);
      }
      if (task.getMisfirePolicy() == null) {
        // 默认什么都不做
        task.setMisfirePolicy(TriggerType.SimplePolicy.SMART_POLICY.getPolicy());
      }
    }

    if (task.getEndAt() != null) {
      // 至少开始后的5秒再结束
      task.setEndAt(Math.max(task.getStartAt() + 5_000, task.getEndAt()));
    }
    if (task.getRecovery() == null) {
      task.setRecovery(Boolean.FALSE);
    }
    if (task.getPersistent() == null) {
      task.setPersistent(false);
    }
    if (task.getDisallowConcurrent() == null) {
      task.setDisallowConcurrent(false);
    }
    if (task.getPriority() == null) {
      task.setPriority(QuartzJobTask.TRIGGER_PRIORITY);
    }

    // job类型
    JobType jobType = task.getJobType() != null ? task.getJobType() : JobType.DEFAULT;
    // 任务类型
    task.setJobType(jobType);
    // 是否持久化
    task.setPersistent(jobType.isPersistent());
    // 不允许并发执行
    task.setDisallowConcurrent(jobType.isDisallowConcurrent());

    return task;
  }

  /**
   * 检查 task 的 worker
   *
   * @param task
   */
  public static void checkWorker(QuartzJobTask task) {
    WorkerType workerType = task.getWorkerType();
    if (workerType == null) {
      throw new QuartzException("WorkerType错误!");
    }

    if (StringUtils.isBlank(task.getWorker())) {
      throw new QuartzException("worker不能为空");
    }

    if (workerType != WorkerType.SPRING_BEAN_NAME) {
      try {
        Class.forName(task.getWorker());
      } catch (ClassNotFoundException e) {
        throw new QuartzException("请指定正确的worker");
      }
    } else {
      String worker = task.getWorker();
      ApplicationContext ctx = SpringCtxHolder.getCtx();
      if (!(ctx.containsBean(worker)) || !(ctx.getBean(task.getWorker()) instanceof JobWorker)) {
        throw new QuartzException("未发现匹配的JobWorker实例!");
      }
    }
  }

  /**
   * 构建 JobDetail
   *
   * @param task 任务
   * @return 返回 JobBuilder
   */
  public static JobBuilder job(QuartzJobTask task) {
    checkWorker(task);
    // 创建 JobDetails
    JobBuilder jb = JobBuilder.newJob();
    jb.ofType(task.getJobType().getJobClass());
    jb.withIdentity(task.getJobName(), task.getJobGroup());
    jb.withDescription(task.getDescription());
    jb.requestRecovery(task.getRecovery());
    // 不持久化
    jb.storeDurably(false);
    jb.usingJobData(JobWorker.KEY_ID, task.getId());
    jb.usingJobData(JobWorker.KEY_JOB_DATA, task.getJobData());
    jb.usingJobData(JobWorker.KEY_WORKER, task.getWorker());
    jb.usingJobData(JobWorker.KEY_WORKER_TYPE, task.getWorkerType().name());
    return jb;
  }

  /**
   * 构建触发器
   *
   * @param task 任务
   * @return 返回TriggerBuilder
   */
  public static TriggerBuilder<Trigger> trigger(QuartzJobTask task) {
    TriggerType triggerType = task.getTriggerType();
    if (triggerType == null) {
      throw new QuartzException("触发器类型错误");
    }
    return triggerType == TriggerType.CRON ? cronTrigger(task) : simpleTrigger(task);
  }

  /**
   * 构建触发器
   *
   * @param task 任务
   * @return 返回TriggerBuilder
   */
  public static TriggerBuilder<Trigger> trigger(QuartzJobTask task, TriggerBuilder<Trigger> tb) {
    tb.withIdentity(task.getTriggerName(), task.getTriggerGroup());
    // 触发器优先级
    tb.withPriority(task.getPriority());
    // 开始执行的时间
    if (task.getStartAt() != null) {
      tb.startAt(new Date(task.getStartAt()));
    } else {
      tb.startNow();
    }
    // 结束时间
    if (task.getEndAt() != null) {
      tb.endAt(new Date(task.getEndAt()));
    }
    if (StringUtils.isNotBlank(task.getCalendarName())) {
      tb.modifiedByCalendar(task.getCalendarName());
    }
    return tb;
  }

  /**
   * 构建触发器
   *
   * @param task 任务
   * @return 返回TriggerBuilder
   */
  public static TriggerBuilder<Trigger> simpleTrigger(QuartzJobTask task) {
    TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger();
    trigger(task, tb);
    // 调度器
    SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();
    TriggerType.SimplePolicy.schedulePolicy(ssb, task.getMisfirePolicy());
    ssb.withIntervalInMilliseconds(task.getSimpleInterval());
    ssb.withRepeatCount(task.getSimpleRepeatCount());
    tb.withSchedule(ssb);
    return tb;
  }

  /**
   * 构建触发器
   *
   * @param task 任务
   * @return 返回TriggerBuilder
   */
  public static TriggerBuilder<Trigger> cronTrigger(QuartzJobTask task) {
    TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger();
    trigger(task, tb);
    // 调度器
    CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule(task.getCronExpression());
    TriggerType.CronPolicy.schedulePolicy(csb, task.getMisfirePolicy());
    tb.withSchedule(csb);
    return tb;
  }

  /**
   * 触发器的Key
   */
  public static JobKey jobKey(QuartzJobTask task) {
    return new JobKey(task.getJobName(), task.getJobGroup());
  }

  /**
   * 触发器的Key
   */
  public static TriggerKey triggerKey(QuartzJobTask task) {
    return new TriggerKey(task.getTriggerName(), task.getTriggerGroup());
  }

  public static void scheduleJob(Scheduler scheduler, QuartzJobTask task) throws IllegalStateException {
    try {
      // 创建 JobDetails
      if (scheduler.checkExists(jobKey(task))) {
        scheduler.resumeJob(jobKey(task));
        return;
      }

      JobDetail jd = job(task).build();
      Trigger trigger = scheduler.getTrigger(triggerKey(task));
      if (trigger == null) {
        trigger = trigger(task).forJob(jd).build();
      }

      // 添加调度器
      scheduler.scheduleJob(jd, trigger);

      // 是否暂停job
      if (!Boolean.TRUE.equals(task.getActive())) {
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
