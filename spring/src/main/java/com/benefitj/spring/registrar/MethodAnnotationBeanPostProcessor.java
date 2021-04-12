package com.benefitj.spring.registrar;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MethodAnnotationBeanPostProcessor implements AnnotationBeanPostProcessor {

  /**
   * 缓存解析的注解信息
   */
  private final ConcurrentMap<Class<?>, AnnotationMetadata> typeMethodMetadatas = new ConcurrentHashMap<>();

  private ConfigurableListableBeanFactory listableBeanFactory;

  private MetadataResolver metadataResolver;

  public MethodAnnotationBeanPostProcessor() {
  }

  public MethodAnnotationBeanPostProcessor(MetadataResolver metadataResolver) {
    this.metadataResolver = metadataResolver;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    if (beanFactory instanceof ConfigurableListableBeanFactory) {
      this.setListableBeanFactory((ConfigurableListableBeanFactory) beanFactory);
    }
  }

  @Override
  public void afterSingletonsInstantiated() {
    // 实例化
    ConcurrentMap<Class<?>, AnnotationMetadata> cache = getTypeMethodMetadatas();
    if (!cache.isEmpty()) {
      try {
        doProcessAnnotations(cache, getListableBeanFactory());
      } finally {
        cache.clear();
      }
    }
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    if (support(targetClass, bean, beanName)) {
      ConcurrentMap<Class<?>, AnnotationMetadata> cache = getTypeMethodMetadatas();
      if (!cache.containsKey(targetClass)) {
        // 扫描
        MetadataResolver resolver = getMetadataResolver();
        if (resolver != null) {
          AnnotationMetadata metadata = resolver.resolve(targetClass, bean, beanName, getListableBeanFactory());
          if (metadata != null) {
            cache.put(bean.getClass(), metadata);
          }
        }
      }
    }
    return bean;
  }

  /**
   * 判断是否支持解析
   *
   * @param targetClass bean class
   * @param bean        bean实例
   * @param beanName    bean名称
   * @return 返回判断的结果
   */
  public boolean support(Class<?> targetClass, Object bean, String beanName) {
    return false;
  }

  /**
   * 处理注解
   *
   * @param typeMetadatas 注解元数据
   * @param beanFactory     beanFactory对象
   */
  protected void doProcessAnnotations(ConcurrentMap<Class<?>, AnnotationMetadata> typeMetadatas,
                                      ConfigurableListableBeanFactory beanFactory) {
    // 处理结果
  }


  public ConfigurableListableBeanFactory getListableBeanFactory() {
    return listableBeanFactory;
  }

  public void setListableBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    this.listableBeanFactory = beanFactory;
  }

  public MetadataResolver getMetadataResolver() {
    return metadataResolver;
  }

  public void setMetadataResolver(MetadataResolver metadataResolver) {
    this.metadataResolver = metadataResolver;
  }

  public ConcurrentMap<Class<?>, AnnotationMetadata> getTypeMethodMetadatas() {
    return typeMethodMetadatas;
  }
}
