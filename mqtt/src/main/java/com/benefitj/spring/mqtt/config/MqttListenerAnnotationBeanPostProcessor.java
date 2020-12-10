package com.benefitj.spring.mqtt.config;

import com.benefitj.spring.mqtt.MqttMessageListenerEndpointRegistry;
import com.benefitj.spring.mqtt.annotaion.MqttMessageListenerEndpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MqttListenerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered,
    BeanFactoryAware, SmartInitializingSingleton {

  private static final ConversionService CONVERSION_SERVICE = new DefaultConversionService();

  private final Log logger = LogFactory.getLog(this.getClass());

  private BeanFactory beanFactory;
  /**
   *
   */
  private final ConcurrentMap<Class<?>, EndpointTypeMetadata> typeCache = new ConcurrentHashMap<>();

  private MessageHandlerMethodFactory handlerMethodFactory;

  @Override
  public int getOrder() {
    return LOWEST_PRECEDENCE;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void afterSingletonsInstantiated() {
    // 注册
    this.typeCache.forEach(this::registerEndpoint);
  }

  /**
   * 注册
   *
   * @param beanClass    bean类
   * @param typeMetadata 类型信息
   */
  protected void registerEndpoint(Class<?> beanClass, EndpointTypeMetadata typeMetadata) {
    MqttMessageListenerEndpointRegistry registry = beanFactory.getBean(MqttMessageListenerEndpointRegistry.class);
    for (EndpointTypeMetadata.ListenerMethod method : typeMetadata.getListenerMethods()) {
      registry.registerEndpoint(typeMetadata.getBean(), typeMetadata.getBeanName(),
          method.getMethod(), method.getAnnotation(), getHandlerMethodFactory());
    }
  }

  public void setHandlerMethodFactory(MessageHandlerMethodFactory handlerMethodFactory) {
    this.handlerMethodFactory = handlerMethodFactory;
  }

  public MessageHandlerMethodFactory getHandlerMethodFactory() {
    if (this.handlerMethodFactory == null) {
      this.handlerMethodFactory = createMessageHandlerMethodFactory();
    }
    return this.handlerMethodFactory;
  }

  protected MessageHandlerMethodFactory createMessageHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory defaultFactory = new DefaultMessageHandlerMethodFactory();
    defaultFactory.setBeanFactory(this.beanFactory);
    DefaultConversionService conversionService = new DefaultConversionService();
    conversionService.addConverter(new BytesToStringConverter());
    defaultFactory.setConversionService(conversionService);
    defaultFactory.afterPropertiesSet();
    return defaultFactory;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
    // 扫描bean上的方法
    this.typeCache.computeIfAbsent(AopUtils.getTargetClass(bean), cls -> buildMetadata(bean, beanName, cls));
    return bean;
  }

  protected EndpointTypeMetadata buildMetadata(final Object bean, final String beanName, Class<?> targetClass) {
    final List<EndpointTypeMetadata.ListenerMethod> methods = new ArrayList<>();
    ReflectionUtils.doWithMethods(targetClass, method -> {
      MqttMessageListenerEndpoint listenerAnnotation = findListenerAnnotation(method);
      if (listenerAnnotation != null) {
        Method methodToUse = checkProxy(method, bean);
        methods.add(new EndpointTypeMetadata.ListenerMethod(methodToUse, listenerAnnotation));
      }
    }, ReflectionUtils.USER_DECLARED_METHODS);
    if (methods.isEmpty()) {
      return EndpointTypeMetadata.EMPTY;
    }
    return new EndpointTypeMetadata(bean, beanName, methods.toArray(new EndpointTypeMetadata.ListenerMethod[methods.size()]));
  }

  protected MqttMessageListenerEndpoint findListenerAnnotation(AnnotatedElement element) {
    return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
        .stream(MqttMessageListenerEndpoint.class)
        .map(MergedAnnotation::synthesize)
        .findFirst()
        .orElse(null);
  }


  private Method checkProxy(Method methodArg, Object bean) {
    Method method = methodArg;
    if (AopUtils.isJdkDynamicProxy(bean)) {
      try {
        // Found a @RabbitListener method on the target class for this JDK proxy ->
        // is it also present on the proxy itself?
        method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
        Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
        for (Class<?> iface : proxiedInterfaces) {
          try {
            method = iface.getMethod(method.getName(), method.getParameterTypes());
            break;
          } catch (@SuppressWarnings("unused") NoSuchMethodException noMethod) {
          }
        }
      } catch (SecurityException ex) {
        ReflectionUtils.handleReflectionException(ex);
      } catch (NoSuchMethodException ex) {
        throw new IllegalStateException(String.format(
            "@MqttMessageListenerEndpoint method '%s' found on bean target class '%s', " +
                "but not found in any interface(s) for a bean JDK proxy. Either " +
                "pull the method up to an interface or switch to subclass (CGLIB) " +
                "proxies by setting proxy-target-class/proxyTargetClass " +
                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
      }
    }
    return method;
  }

  public static class BytesToStringConverter implements Converter<byte[], String> {
    @Override
    public String convert(byte[] source) {
      return new String(source, StandardCharsets.UTF_8);
    }
  }

}
