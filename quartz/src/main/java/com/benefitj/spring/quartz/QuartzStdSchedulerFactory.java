package com.benefitj.spring.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Collection;
import java.util.Properties;
import java.util.stream.Collectors;

public class QuartzStdSchedulerFactory extends StdSchedulerFactory {

  public QuartzStdSchedulerFactory() {
  }

  public QuartzStdSchedulerFactory(Properties props) throws SchedulerException {
    super(props);
  }

  public QuartzStdSchedulerFactory(String fileName) throws SchedulerException {
    super(fileName);
  }

  @Override
  protected Scheduler instantiate(QuartzSchedulerResources rsrcs, QuartzScheduler qs) {
    return IScheduler.create(new StdScheduler(qs));
  }

  @Override
  public Scheduler getScheduler() {
    try {
      Scheduler scheduler = super.getScheduler();
      return wrapProxy(scheduler);
    } catch (SchedulerException e) {
      throw new QuartzException(e);
    }
  }

  @Override
  public Scheduler getScheduler(String schedName) {
    try {
      Scheduler scheduler = super.getScheduler(schedName);
      return wrapProxy(scheduler);
    } catch (SchedulerException e) {
      throw new QuartzException(e);
    }
  }

  @Override
  public Collection<Scheduler> getAllSchedulers() {
    try {
      return super.getAllSchedulers()
          .stream()
          .map(this::wrapProxy)
          .collect(Collectors.toList());
    } catch (SchedulerException e) {
      throw new QuartzException(e);
    }
  }

  public Scheduler wrapProxy(Scheduler scheduler) {
    if (scheduler instanceof IScheduler) {
      return scheduler;
    }
    try {
      synchronized (this) {
        SchedulerRepository schedRep = SchedulerRepository.getInstance();
        schedRep.remove(scheduler.getSchedulerName());
        // 重新添加
        schedRep.bind(scheduler = IScheduler.create(scheduler));
      }
    } catch (SchedulerException e) {
      throw new QuartzException(e);
    }
    return scheduler;
  }

}
