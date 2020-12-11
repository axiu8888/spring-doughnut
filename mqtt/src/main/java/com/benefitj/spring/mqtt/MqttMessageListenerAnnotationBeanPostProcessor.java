package com.benefitj.spring.mqtt;

import com.benefitj.spring.registrar.AnnotationTypeMetadata;
import com.benefitj.spring.registrar.MethodAnnotationBeanPostProcessor;
import com.benefitj.spring.registrar.TypeMetadataResolver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * MQTT消息订阅注解的后置处理器
 *
 * @author DINGXIUAN
 */
public class MqttMessageListenerAnnotationBeanPostProcessor extends MethodAnnotationBeanPostProcessor implements TypeMetadataResolver {

  public MqttMessageListenerAnnotationBeanPostProcessor() {
    this.setMetadataResolver(this);
  }

  @Override
  public boolean support(Class<?> targetClass, Object bean, String beanName) {
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public AnnotationTypeMetadata resolve(Class<?> targetClass, Object bean, String beanName, BeanFactory beanFactory) {
    Collection<Method> methods = findAnnotationMethods(targetClass, MqttMessageListener.class);
    if (methods.isEmpty()) {
      return null;
    }
    AnnotationTypeMetadata.MethodElement[] elements = methods.stream()
        .map(method -> new AnnotationTypeMetadata.MethodElement(
            method, method.getAnnotationsByType(MqttMessageListener.class)))
        .toArray(AnnotationTypeMetadata.MethodElement[]::new);
    return new AnnotationTypeMetadata(targetClass, bean, beanName, elements);
  }

  @Override
  protected void doProcessAnnotations(ConcurrentMap<Class<?>, AnnotationTypeMetadata> typeMetadatas, ConfigurableListableBeanFactory beanFactory) {
    // 注册
    MqttMessageListenerRegistrar registrar = beanFactory.getBean(MqttMessageListenerRegistrar.class);
    typeMetadatas.values()
        .stream()
        .filter(atm -> atm.getMethodElements().length > 0)
        .forEach(atm -> registrar.register(atm, beanFactory));
  }

}
