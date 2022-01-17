//package com.benefitj.system.security;
//
//import com.benefitj.spring.mvc.mapping.ApiDescriptor;
//import com.benefitj.spring.mvc.mapping.MappingAnnotationMetadata;
//import com.benefitj.spring.mvc.mapping.MappingAnnotationResolver;
//import org.springframework.core.annotation.AnnotationUtils;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import java.lang.reflect.Method;
//
//public class ResourceAnnotationResolver extends MappingAnnotationResolver {
//
//  public ResourceAnnotationResolver() {
//    super();
//  }
//
//  @Override
//  public boolean support(Object bean, Method method) {
//    if (!method.isAnnotationPresent(ResourceTag.class)) {
//      return false;
//    }
//    return super.support(bean, method);
//  }
//
//  @Override
//  protected ApiDescriptor resolveApi(MappingAnnotationMetadata metadata, String[] baseUrls, String[] paths, RequestMethod... httpMethods) {
//    ApiDescriptor ad = super.resolveApi(metadata, baseUrls, paths, httpMethods);
//    ResourceTag resourceTag = AnnotationUtils.getAnnotation(metadata.getMethod(), ResourceTag.class);
//    return new ResourceDescriptor(ad.getApiOperation(), ad.getPaths(), ad.getHttpMethods(), resourceTag);
//  }
//
//}
