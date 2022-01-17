package com.benefitj.spring.mvc.mapping;

import io.swagger.annotations.ApiOperation;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * API描述
 */
public class ApiDescriptor {

  /**
   * 请求映射
   */
  private Annotation mapping;
  /**
   * API操作
   */
  private ApiOperation apiOperation;
  /**
   * 路径
   */
  private List<String> paths;
  /**
   * HTTP方法
   */
  private List<String> methods;

  public ApiDescriptor() {
  }

  public Annotation getMapping() {
    return mapping;
  }

  public void setMapping(Annotation mapping) {
    this.mapping = mapping;
  }

  public ApiOperation getApiOperation() {
    return apiOperation;
  }

  public void setApiOperation(ApiOperation apiOperation) {
    this.apiOperation = apiOperation;
  }

  public List<String> getPaths() {
    return paths;
  }

  public void setPaths(List<String> paths) {
    this.paths = paths;
  }

  public List<String> getMethods() {
    return methods;
  }

  public void setMethods(List<String> methods) {
    this.methods = methods;
  }
}
