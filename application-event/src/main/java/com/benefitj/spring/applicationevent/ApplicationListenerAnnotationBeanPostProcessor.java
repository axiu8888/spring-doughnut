package com.benefitj.spring.applicationevent;

import com.benefitj.spring.registrar.AnnotationTypeMetadata;
import com.benefitj.spring.registrar.SingleAnnotationBeanPostProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationEvent;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 注解处理器
 */
public class ApplicationListenerAnnotationBeanPostProcessor extends SingleAnnotationBeanPostProcessor {

  private static final AtomicInteger INDEXER = new AtomicInteger();

  private ApplicationEventAdapterHandler adapterHandler;

  public ApplicationListenerAnnotationBeanPostProcessor(ApplicationEventAdapterHandler adapterHandler) {
    this.setAdapterHandler(adapterHandler);
    this.setAnnotationType(ApplicationEventListener.class);
  }

  @Override
  protected void doFinalProcessAnnotations(AnnotationTypeMetadata metadata, ConfigurableListableBeanFactory beanFactory) {
    Object bean = metadata.getBean();
    for (AnnotationTypeMetadata.MethodElement element : metadata.getMethodElements()) {
      Method method = element.getMethod();
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (parameterTypes.length != 1 || !ApplicationEvent.class.isAssignableFrom(parameterTypes[0])) {
        String paramTypes = Stream.of(parameterTypes)
            .map(Class::getSimpleName)
            .collect(Collectors.joining(", "));
        throw new IllegalArgumentException("不支持的方法：" + metadata.getTargetClass().getName()
            + method.getName() + ".(" + paramTypes + "), 仅支持单个的ApplicationEvent类型的对象");
      }

      ApplicationEventListener listener = (ApplicationEventListener)element.getAnnotations()[0];

      EventAdapterFactory factory;
      if (StringUtils.isNotBlank(listener.adapterFactoryName())) {
        factory = beanFactory.getBean(listener.adapterFactoryName(), listener.adapterFactory());
      } else {
        factory = beanFactory.getBean(listener.adapterFactory());
      }

      ApplicationEventAdapter adapter = factory.create(bean, method, (Class<ApplicationEvent>) parameterTypes[0]);
      // 注册
      String beanName = metadata.getBeanName() + "." + method.getName() + "_" + INDEXER.incrementAndGet();
      getAdapterHandler().register(beanName, adapter);
    }
  }

  public ApplicationEventAdapterHandler getAdapterHandler() {
    return adapterHandler;
  }

  public void setAdapterHandler(ApplicationEventAdapterHandler adapterHandler) {
    this.adapterHandler = adapterHandler;
  }

}
