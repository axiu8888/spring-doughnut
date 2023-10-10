package com.benefitj.spring.quartz.worker;

import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.AnnotationResolverImpl;
import com.benefitj.spring.annotation.MetadataHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * QuartzJob 注解处理器
 */
public class QuartzWorkerProcessor extends AnnotationBeanProcessor implements MetadataHandler, DisposableBean {

  QuartzWorkerManager manager;

  public QuartzWorkerProcessor(QuartzWorkerManager manager) {
    this.manager = manager;
    this.setMetadataHandler(this);
    this.setResolver(new AnnotationResolverImpl(QuartzWorker.class));
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      Method method = metadata.getMethod();
      QuartzWorker quartzWorker = method.getAnnotation(QuartzWorker.class);
      String name = quartzWorker.name();
      String classMethod = method.getDeclaringClass().getName() + "." + method.getName();
      if (StringUtils.isBlank(name)) {
        throw new IllegalStateException("[ @" + QuartzWorker.class.getSimpleName() + " ]缺少名称: " + classMethod);
      }
      if (manager.containsKey(name)) {
        QuartzWorkerInvoker existInvoker = manager.get(name);
        Method method2 = existInvoker.getMethod();
        String classMethod2 = method2.getDeclaringClass().getName() + "." + method2.getName();
        throw new IllegalStateException("存在相同名称的[ @QuartzJob ]: " + classMethod2 + " & " + classMethod);
      }
      QuartzWorkerInvoker invoker = new QuartzWorkerInvoker();
      invoker.setName(name);
      invoker.setMetadata(metadata);
      invoker.setBean(metadata.getBean());
      invoker.setArgDescriptors(mapArgs(metadata.getMethod()));
      manager.put(name, invoker);
    }
  }

  public List<ArgDescriptor> mapArgs(Method method) {
    Parameter[] parameters = method.getParameters();
    List<ArgDescriptor> args = new ArrayList<>(parameters.length);
    for (int i = 0; i < parameters.length; i++) {
      Parameter p = parameters[i];
      QuartzWorkerArg annotation = p.getAnnotation(QuartzWorkerArg.class);
      if (annotation == null) {
        String declaringName = method.getDeclaringClass().getName() + "." + method.getName();
        throw new IllegalStateException("[ " + declaringName + " ] 方法的参数\"" + p.getName() + "\", 需要被 @" + QuartzWorkerArg.class.getSimpleName() + " 注解注释");
      }
      ArgDescriptor ad = new ArgDescriptor(p, i);
      ad.setName(p.getName());
      ad.setType(ArgType.find(p.getType()));
      ad.setAnnotation(annotation);
      ad.setDescription(annotation.description());
      args.add(ad);
    }
    return args;
  }

  @Override
  public void destroy() throws Exception {
  }

}
