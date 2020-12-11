package com.benefitj.spring.eventbus;

import com.benefitj.event.EventBusPoster;
import com.benefitj.spring.registrar.AnnotationTypeMetadata;
import com.benefitj.spring.registrar.MethodAnnotationBeanPostProcessor;
import com.benefitj.spring.registrar.TypeMetadataResolver;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * EventBus监听注解的后置处理器
 *
 * @author DINGXIUAN
 */
public class EventBusListenerAnnotationBeanPostProcessor extends MethodAnnotationBeanPostProcessor implements TypeMetadataResolver {

  public EventBusListenerAnnotationBeanPostProcessor() {
    this.setMetadataResolver(this);
  }

  @Override
  public boolean support(Class<?> targetClass, Object bean, String beanName) {
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public AnnotationTypeMetadata resolve(Class<?> targetClass, Object bean, String beanName, BeanFactory beanFactory) {
    Collection<Method> methods = findAnnotationMethods(targetClass, Subscribe.class);
    if (methods.isEmpty()) {
      return null;
    }
    AnnotationTypeMetadata.MethodElement[] elements = methods.stream()
        .map(method -> new AnnotationTypeMetadata.MethodElement(
            method, method.getAnnotationsByType(Subscribe.class)))
        .toArray(AnnotationTypeMetadata.MethodElement[]::new);
    return new AnnotationTypeMetadata(targetClass, bean, beanName, elements);
  }

  @Override
  protected void doProcessAnnotations(ConcurrentMap<Class<?>, AnnotationTypeMetadata> typeMetadatas, ConfigurableListableBeanFactory beanFactory) {
    // 注册
    typeMetadatas.values()
        .stream()
        .filter(atm -> atm.getMethodElements().length > 0)
        .forEach(atm -> register(atm, beanFactory));
  }

  /**
   * 注册
   *
   * @param metadata    元数据
   * @param beanFactory bean工程
   */
  protected void register(AnnotationTypeMetadata metadata, ConfigurableListableBeanFactory beanFactory) {
    Object bean = metadata.getBean();
    for (AnnotationTypeMetadata.MethodElement element : metadata.getMethodElements()) {
      Method method = element.getMethod();
      EventBusPoster poster;
      if (method.isAnnotationPresent(DefinedPoster.class)) {
        DefinedPoster definedPoster = method.getAnnotation(DefinedPoster.class);
        if (StringUtils.isNotBlank(definedPoster.posterName())) {
          poster = beanFactory.getBean(definedPoster.posterName(), definedPoster.posterType());
        } else {
          poster = beanFactory.getBean(definedPoster.posterType());
        }
      } else {
        poster = beanFactory.getBean(EventBusPoster.class);
      }
      poster.register(bean);
    }
  }

}
