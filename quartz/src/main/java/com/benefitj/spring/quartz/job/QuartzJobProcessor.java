package com.benefitj.spring.quartz.job;

import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.AnnotationResolverImpl;
import com.benefitj.spring.annotation.MetadataHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;

import java.lang.reflect.Method;
import java.util.List;

/**
 * QuartzJob 注解处理器
 */
public class QuartzJobProcessor extends AnnotationBeanProcessor implements MetadataHandler, DisposableBean {

  QuartzJobManager manager;

  public QuartzJobProcessor(QuartzJobManager manager) {
    this.manager = manager;
    this.setMetadataHandler(this);
    this.setResolver(new AnnotationResolverImpl(QuartzJob.class));
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      Method method = metadata.getMethod();
      QuartzJob quartzJob = method.getAnnotation(QuartzJob.class);
      String name = quartzJob.name();
      String classMethod = method.getDeclaringClass().getName() + "." + method.getName();
      if (StringUtils.isBlank(name)) {
        throw new IllegalStateException("[ @QuratzJob ]缺少名称: " + classMethod);
      }
      if (manager.containsKey(name)) {
        QuartzJobInvoker existInvoker = manager.get(name);
        Method method2 = existInvoker.getMethod();
        String classMethod2 = method2.getDeclaringClass().getName() + "." + method2.getName();
        throw new IllegalStateException("存在相同名称的[ @QuratzJob ]: " + classMethod2 + " & " + classMethod);
      }
      QuartzJobInvoker invoker = new QuartzJobInvoker();
      invoker.setName(name);
      invoker.setMetadata(metadata);
      invoker.setBean(metadata.getBean());
      manager.put(name, invoker);
    }
  }

  @Override
  public void destroy() throws Exception {
  }

}
