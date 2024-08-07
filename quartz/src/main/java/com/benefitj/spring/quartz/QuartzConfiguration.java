package com.benefitj.spring.quartz;

import com.benefitj.core.CatchUtils;
import com.benefitj.spring.quartz.worker.QuartzWorkerManager;
import com.benefitj.spring.quartz.worker.QuartzWorkerProcessor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.autoconfigure.quartz.QuartzTransactionManager;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.Map;
import java.util.Properties;

@EnableConfigurationProperties
@PropertySource("classpath:quartz-spring.properties")
@QuartzTransactionManager
@QuartzDataSource
@Configuration
public class QuartzConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public StdSchedulerFactory schedulerFactory(QuartzProperties quartzProperties) {
    final Properties properties = new Properties();
    final Map<String, String> map = quartzProperties.getProperties();
    map.forEach((key, value) -> properties.put(
        key.replaceFirst("spring.quartz.properties.", ""), value));
    try {
      return new QuartzStdSchedulerFactory(properties);
    } catch (SchedulerException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  @Primary
  @ConditionalOnMissingBean
  @Bean
  public SchedulerFactoryBean schedulerFactoryBean(QuartzProperties properties,
                                                   ObjectProvider<SchedulerFactoryBeanCustomizer> customizers,
                                                   @Autowired(required = false) ObjectProvider<JobDetail> jobDetails,
                                                   @Autowired(required = false) Map<String, Calendar> calendars,
                                                   ObjectProvider<Trigger> triggers,
                                                   ApplicationContext context,
                                                   SchedulerFactory schedulerFactory) {
    SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
    schedulerFactoryBean.setSchedulerFactory(schedulerFactory);
    SpringBeanJobFactory jobFactory = new SpringBeanJobFactory();
    jobFactory.setApplicationContext(context);
    schedulerFactoryBean.setJobFactory(jobFactory);
    if (properties.getSchedulerName() != null) {
      schedulerFactoryBean.setSchedulerName(properties.getSchedulerName());
    }
    schedulerFactoryBean.setAutoStartup(properties.isAutoStartup());
    schedulerFactoryBean.setStartupDelay((int) properties.getStartupDelay().getSeconds());
    schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(properties.isWaitForJobsToCompleteOnShutdown());
    schedulerFactoryBean.setOverwriteExistingJobs(properties.isOverwriteExistingJobs());
    if (!properties.getProperties().isEmpty()) {
      schedulerFactoryBean.setQuartzProperties(asProperties(properties.getProperties()));
    }

    schedulerFactoryBean.setSchedulerListeners(
        context.getBeansOfType(SchedulerListener.class)
            .values()
            .toArray(new SchedulerListener[0]));

    schedulerFactoryBean.setGlobalTriggerListeners(
        context.getBeansOfType(TriggerListener.class)
            .values()
            .toArray(new TriggerListener[0]));

    schedulerFactoryBean.setGlobalJobListeners(
        context.getBeansOfType(JobListener.class)
            .values()
            .toArray(new JobListener[0]));

    schedulerFactoryBean.setJobDetails(jobDetails.orderedStream().toArray(JobDetail[]::new));
    schedulerFactoryBean.setCalendars(calendars);
    schedulerFactoryBean.setTriggers(triggers.orderedStream().toArray(Trigger[]::new));
    customizers.orderedStream().forEach(customizer -> customizer.customize(schedulerFactoryBean));
    return schedulerFactoryBean;
  }

  private Properties asProperties(Map<String, String> source) {
    Properties properties = new Properties();
    properties.putAll(source);
    return properties;
  }

  @ConditionalOnMissingBean
  @Bean
  public QuartzWorkerManager quartzJobManager() {
    return QuartzWorkerManager.get();
  }

  @ConditionalOnMissingBean(name = "quartzJobProcessor")
  @Bean("quartzJobProcessor")
  public QuartzWorkerProcessor quartzJobProcessor(QuartzWorkerManager manager) {
    return new QuartzWorkerProcessor(manager);
  }

  @ConditionalOnMissingBean
  @Bean
  public IScheduler ischeduler(Scheduler scheduler) {
    return (IScheduler) scheduler;
  }

}
