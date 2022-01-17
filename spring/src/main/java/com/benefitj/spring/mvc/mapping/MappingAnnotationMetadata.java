package com.benefitj.spring.mvc.mapping;

import com.benefitj.spring.annotation.AnnotationMetadata;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public class MappingAnnotationMetadata extends AnnotationMetadata {
  /**
   * 类上的请求
   */
  private RequestMapping baseMapping;
  /**
   * API
   */
  private Api api;
  /**
   * 方法上的请求
   */
  private RequestMapping mapping;
  /**
   * 方法上的操作描述
   */
  private ApiOperation apiOperation;
  /**
   * 请求接口
   */
  private List<ApiDescriptor> apiDescriptors;

  public MappingAnnotationMetadata() {
  }

  public RequestMapping getBaseMapping() {
    return baseMapping;
  }

  public void setBaseMapping(RequestMapping baseMapping) {
    this.baseMapping = baseMapping;
  }

  public Api getApi() {
    return api;
  }

  public void setApi(Api api) {
    this.api = api;
  }

  public RequestMapping getMapping() {
    return mapping;
  }

  public void setMapping(RequestMapping mapping) {
    this.mapping = mapping;
  }

  public ApiOperation getApiOperation() {
    return apiOperation;
  }

  public void setApiOperation(ApiOperation apiOperation) {
    this.apiOperation = apiOperation;
  }

  public List<ApiDescriptor> getApiDescriptors() {
    return apiDescriptors;
  }

  public void setApiDescriptors(List<ApiDescriptor> apiDescriptors) {
    this.apiDescriptors = apiDescriptors;
  }

}

