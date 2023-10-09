package com.benefitj.spring.quartz.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.spring.quartz.JobWorker;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * quartz 调度
 */
public class QuartzJobWorker implements JobWorker {

  public static final String JOB_NAME = "jobName";
  public static final String JOB_ARGS = "jobArgs";

  /**
   * 调用时的参数
   */
  public static final ThreadLocal<JSONObject> LOCAL_ARGS = ThreadLocal.withInitial(JSONObject::new);

  @Override
  public void execute(JobExecutionContext context, JobDetail jobDetail, String taskId) throws JobExecutionException {
    try {
      JobDataMap dataMap = jobDetail.getJobDataMap();
      String jobName = dataMap.getString(JOB_NAME);
      String jobArgs = dataMap.getString(JOB_ARGS);
      QuartzJobManager manager = getBean(QuartzJobManager.class);
      QuartzJobInvoker invoker = manager.get(jobName);
      if (invoker == null) {
        throw new JobExecutionException("无法找到jobName[" + jobName + "]对应的函数");
      }

      JSONObject localArgs = LOCAL_ARGS.get();
      localArgs.put(JOB_NAME, jobName);
      localArgs.put(JOB_ARGS, jobArgs);
      localArgs.put("context", context);
      localArgs.put("jobDetail", jobDetail);
      localArgs.put("taskId", taskId);
      if (StringUtils.isNotBlank(jobArgs)) {
        JSONObject argsJson = JSON.parseObject(jobArgs);
        localArgs.put("_args", argsJson);
        localArgs.putAll(argsJson);
      }

      Object[] providedArgs = invoker.mapArgs(localArgs);
      invoker.invoke(providedArgs);

    } finally {
      LOCAL_ARGS.remove();
    }
  }

}
