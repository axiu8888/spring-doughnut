package com.benefitj.spring.quartz.worker;

import java.lang.reflect.Parameter;

public class ArgDescriptor {

  private final Parameter parameter;
  /**
   * 参数的位置
   */
  private final int position;
  /**
   * 参数名称
   */
  private String name;
  /**
   * 参数类型
   */
  private ArgType type;
  /**
   * 描述
   */
  private String description;
  /**
   * 注解
   */
  private QuartzWorkerArg annotation;

  public ArgDescriptor(Parameter parameter, int position) {
    this.parameter = parameter;
    this.position = position;
  }

  public Parameter getParameter() {
    return parameter;
  }

  public int getPosition() {
    return position;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArgType getType() {
    return type;
  }

  public void setType(ArgType type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public QuartzWorkerArg getAnnotation() {
    return annotation;
  }

  public void setAnnotation(QuartzWorkerArg annotation) {
    this.annotation = annotation;
  }
}
