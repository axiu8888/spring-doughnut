package com.benefitj.spring.mvc.mapping;

import com.benefitj.spring.annotation.AnnotationMetadata;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedList;
import java.util.List;

public class MappingAnnotationMetadata extends AnnotationMetadata {
  /**
   * 类上的请求
   */
  private RequestMapping baseMapping;
  /**
   * 请求接口
   */
  private final List<ApiDescriptor> descriptors = new LinkedList<>();

  private Api api;

  public MappingAnnotationMetadata() {
  }

  public RequestMapping getBaseMapping() {
    return baseMapping;
  }

  public void setBaseMapping(RequestMapping baseMapping) {
    this.baseMapping = baseMapping;
  }

  public String[] getBaseUrls() {
    return getBaseMapping().value();
  }

  public List<ApiDescriptor> getDescriptors() {
    return descriptors;
  }

  public void addDescriptors(List<ApiDescriptor> descriptors) {
    this.descriptors.addAll(descriptors);
  }

  public Api getApi() {
    return api;
  }

  public void setApi(Api api) {
    this.api = api;
  }
}
