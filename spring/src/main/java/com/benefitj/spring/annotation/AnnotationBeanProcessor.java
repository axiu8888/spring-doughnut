package com.benefitj.spring.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.lang.annotation.Annotation;

/**
 * 注解的处理器
 */
public class AnnotationBeanProcessor extends AnnotationSearcher implements SmartInitializingSingleton {

  /**
   * 元数据处理器
   */
  private MetadataHandler metadataHandler;
  /**
   * 已经初始化过了
   */
  private volatile boolean initialized = false;

  public AnnotationBeanProcessor() {
  }

  public AnnotationBeanProcessor(Class<? extends Annotation> annotationType,
                                 MetadataHandler metadataHandler) {
    this.metadataHandler = metadataHandler;
    register(annotationType);
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Object returnBean = super.postProcessAfterInitialization(bean, beanName);
    if (isInitialized()) {
      // 直接注册
      getMetadataHandler().handle(getMetadatas());
    }
    return returnBean;
  }

  @Override
  public void afterSingletonsInstantiated() {
    setInitialized(true);
    getMetadataHandler().handle(getMetadatas());
  }

  public MetadataHandler getMetadataHandler() {
    return metadataHandler;
  }

  public void setMetadataHandler(MetadataHandler metadataHandler) {
    this.metadataHandler = metadataHandler;
  }

  public boolean isInitialized() {
    return initialized;
  }

  public void setInitialized(boolean initialized) {
    this.initialized = initialized;
  }

}