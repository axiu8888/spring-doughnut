package com.benefitj.spring.annotation;

import org.springframework.beans.factory.SmartInitializingSingleton;

import java.lang.annotation.Annotation;

/**
 * 注解的处理器
 */
public class AnnotationBeanProcessor extends AnnotationSearcher implements SmartInitializingSingleton {

  private MetadataHandler metadataHandler;

  public AnnotationBeanProcessor() {
  }

  public AnnotationBeanProcessor(Class<? extends Annotation> annotationType,
                                 MetadataHandler metadataHandler) {
    this.metadataHandler = metadataHandler;
    register(annotationType);
  }

  @Override
  public void afterSingletonsInstantiated() {
    getMetadataHandler().handle(getMetadatas());
  }

  public MetadataHandler getMetadataHandler() {
    return metadataHandler;
  }

  public void setMetadataHandler(MetadataHandler metadataHandler) {
    this.metadataHandler = metadataHandler;
  }
}