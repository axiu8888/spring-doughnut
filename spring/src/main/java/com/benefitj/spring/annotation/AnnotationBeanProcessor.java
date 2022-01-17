package com.benefitj.spring.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;

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

  public AnnotationBeanProcessor(AnnotationResolver resolver,
                                 MetadataHandler metadataHandler) {
    super(resolver);
    this.metadataHandler = metadataHandler;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Object returnBean = super.postProcessAfterInitialization(bean, beanName);
    if (isInitialized()) {
      // 直接注册
      onHandleMetadata();
    }
    return returnBean;
  }

  @Override
  public void afterSingletonsInstantiated() {
    setInitialized(true);
    onHandleMetadata();
  }

  protected void onHandleMetadata() {
    MetadataHandler handler = getMetadataHandler();
    if (handler == null) {
      throw new IllegalStateException("【 "+ getResolver() +" 】未发现【 MetadataHandler 】...");
    }
    handler.handle(getMetadatas());
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