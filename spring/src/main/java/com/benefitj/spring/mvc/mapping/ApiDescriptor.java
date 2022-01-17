package com.benefitj.spring.mvc.mapping;

import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * API描述
 */
public class ApiDescriptor {

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
  private String[] httpMethods;

  public ApiDescriptor() {
  }

  public ApiDescriptor(ApiOperation apiOperation, List<String> paths, String[] httpMethods) {
    this.apiOperation = apiOperation;
    this.paths = paths;
    this.httpMethods = httpMethods;
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

  public String[] getHttpMethods() {
    return httpMethods;
  }

  public void setHttpMethods(String[] httpMethods) {
    this.httpMethods = httpMethods;
  }
}
