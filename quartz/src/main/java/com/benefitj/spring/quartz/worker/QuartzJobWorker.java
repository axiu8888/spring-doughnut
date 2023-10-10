package com.benefitj.spring.quartz.worker;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.spring.quartz.JobWorker;
import com.benefitj.spring.quartz.QuartzJobTask;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * quartz 调度
 */
public class QuartzJobWorker implements JobWorker {

  /**
   * 调用时的参数
   */
  public static final ThreadLocal<JSONObject> LOCAL_ARGS = ThreadLocal.withInitial(JSONObject::new);

  @Override
  public void execute(JobExecutionContext context, JobDetail jobDetail, QuartzJobTask task) throws JobExecutionException {
    try {
      String name = task.getWorker();
      QuartzWorkerManager manager = getBean(QuartzWorkerManager.class);
      QuartzWorkerInvoker invoker = manager.get(name);
      if (invoker == null) {
        throw new JobExecutionException("无法找到worker name [" + name + "]对应的函数");
      }

      JSONObject localArgs = LOCAL_ARGS.get();
      localArgs.put("context", context);
      localArgs.put("jobDetail", jobDetail);
      localArgs.put("task", task);
      if (StringUtils.isNotBlank(task.getJobData())) {
        localArgs.putAll(JSON.parseObject(task.getJobData()));
      }

      Object[] providedArgs = invoker.mapArgs(localArgs);
      invoker.invoke(providedArgs);

    } finally {
      LOCAL_ARGS.remove();
    }
  }

}
