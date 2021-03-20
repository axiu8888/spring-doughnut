package com.benefitj.spring.quartzservice;

import com.benefitj.spring.registrar.AnnotationListenerRegistrar;
import com.benefitj.spring.registrar.AnnotationTypeMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Method;

@Slf4j
public class QuartzServiceRegistrar implements AnnotationListenerRegistrar {

  /**
   * 注册
   *
   * @param metadata    注解类的信息
   * @param beanFactory bean工厂
   */
  @Override
  public void register(AnnotationTypeMetadata metadata, ConfigurableListableBeanFactory beanFactory) {
    //Object bean = metadata.getBean();
    for (AnnotationTypeMetadata.MethodElement element : metadata.getMethodElements()) {
      Method method = element.getMethod();
      QuartzService quartzService = (QuartzService) element.getAnnotations()[0];
      // 处理被注解的对象
      log.info("{}.{}, name: {}, cron: {}, remarks: {}"
          , method.getDeclaringClass().getName()
          , method.getName()
          , quartzService.name()
          , quartzService.cron()
          , quartzService.remarks()
      );
    }
  }

}
