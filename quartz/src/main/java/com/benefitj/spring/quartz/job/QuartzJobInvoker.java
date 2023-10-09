package com.benefitj.spring.quartz.job;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.executable.MethodInvokerImpl;
import com.benefitj.spring.annotation.AnnotationMetadata;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class QuartzJobInvoker extends MethodInvokerImpl {

  private String name;
  private AnnotationMetadata metadata;
  private Object bean;
  private List<ArgDescriptor> argDescriptors = new ArrayList<>();

  public QuartzJobInvoker() {
  }

  public QuartzJobInvoker(String name, AnnotationMetadata metadata, Object bean) {
    this.name = name;
    this.metadata = metadata;
    this.bean = bean;
  }

  public Object invoke(Object... providedArgs) {
    return super.invoke(getBean(), getMethod(), providedArgs);
  }

  public AnnotationMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(AnnotationMetadata metadata) {
    this.metadata = metadata;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getBean() {
    return bean;
  }

  public void setBean(Object bean) {
    this.bean = bean;
  }

  public Method getMethod() {
    return getMetadata().getMethod();
  }

  public Parameter[] getParameters() {
    return getMethod().getParameters();
  }

  public QuartzJob getAnnotation() {
    return getMethod().getAnnotation(QuartzJob.class);
  }

  public String getDescription() {
    return getAnnotation().description();
  }

  public List<ArgDescriptor> getArgDescriptors() {
    return argDescriptors;
  }

  public void setArgDescriptors(List<ArgDescriptor> argDescriptors) {
    this.argDescriptors = argDescriptors;
  }

  public Object[] mapArgs(JSONObject args) {
    return getArgDescriptors()
        .stream()
        .map(ad -> args.getObject(ad.getName(), ad.getParameter().getType()))
        .toArray(Object[]::new);
  }

}
