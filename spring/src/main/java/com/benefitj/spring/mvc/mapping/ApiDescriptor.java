package com.benefitj.spring.mvc.mapping;

import io.swagger.annotations.ApiOperation;

import java.util.LinkedList;
import java.util.List;

public class ApiDescriptor {

  private ApiOperation apiOperation;

  private final List<String> paths = new LinkedList<>();

  private String[] httpMethods;

  public ApiDescriptor() {
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

  public String[] getHttpMethods() {
    return httpMethods;
  }

  public void setHttpMethods(String[] httpMethods) {
    this.httpMethods = httpMethods;
  }
}
