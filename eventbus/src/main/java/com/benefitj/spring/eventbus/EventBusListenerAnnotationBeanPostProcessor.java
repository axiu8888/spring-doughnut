package com.benefitj.spring.eventbus;

import com.benefitj.event.EventBusPoster;
import com.benefitj.spring.registrar.AnnotationTypeMetadata;
import com.benefitj.spring.registrar.SingleAnnotationBeanPostProcessor;
import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Method;

/**
 * EventBus监听注解的后置处理器
 *
 * @author DINGXIUAN
 */
public class EventBusListenerAnnotationBeanPostProcessor extends SingleAnnotationBeanPostProcessor {

  public EventBusListenerAnnotationBeanPostProcessor() {
    this.setAnnotationType(Subscribe.class);
  }

  /**
   * 注册
   *
   * @param metadata    元数据
   * @param beanFactory bean工程
   */
  @Override
  protected void doProcessAnnotations0(AnnotationTypeMetadata metadata, ConfigurableListableBeanFactory beanFactory) {
    Object bean = metadata.getBean();
    for (AnnotationTypeMetadata.MethodElement element : metadata.getMethodElements()) {
      Method method = element.getMethod();
      EventBusPoster poster;
      if (method.isAnnotationPresent(DefinedPoster.class)) {
        DefinedPoster definedPoster = method.getAnnotation(DefinedPoster.class);
        if (StringUtils.isNotBlank(definedPoster.name())) {
          poster = beanFactory.getBean(definedPoster.name(), definedPoster.type());
        } else {
          poster = beanFactory.getBean(definedPoster.type());
        }
      } else {
        poster = beanFactory.getBean(EventBusPoster.class);
      }
      poster.register(bean);
    }
  }

}
