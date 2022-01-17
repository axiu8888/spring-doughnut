//package com.benefitj.system.security;
//
//import com.benefitj.spring.mvc.mapping.ApiDescriptor;
//import io.swagger.annotations.ApiOperation;
//
//import java.util.List;
//
//public class ResourceDescriptor extends ApiDescriptor {
//
//  /**
//   * 资源标志
//   */
//  private ResourceTag resourceTag;
//
//  public ResourceDescriptor() {
//  }
//
//  public ResourceDescriptor(ApiOperation apiOperation,
//                            List<String> paths,
//                            String[] httpMethods,
//                            ResourceTag resourceTag) {
//    super(apiOperation, paths, httpMethods);
//    this.resourceTag = resourceTag;
//  }
//
//  public ResourceTag getResourceTag() {
//    return resourceTag;
//  }
//
//  public void setResourceTag(ResourceTag resourceTag) {
//    this.resourceTag = resourceTag;
//  }
//}
