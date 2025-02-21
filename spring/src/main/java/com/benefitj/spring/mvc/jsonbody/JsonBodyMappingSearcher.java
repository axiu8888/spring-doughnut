package com.benefitj.spring.mvc.jsonbody;

import com.benefitj.core.ReflectUtils;
import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.AnnotationResolverImpl;
import com.benefitj.spring.annotation.MetadataHandler;
import com.benefitj.spring.mvc.mapping.MappingAnnotationMetadata;
import com.benefitj.spring.mvc.mapping.MappingAnnotationResolver;
import com.benefitj.spring.mvc.matcher.AntPathRequestMatcher;
import com.benefitj.spring.mvc.matcher.OrRequestMatcher;
import com.benefitj.spring.mvc.matcher.RequestMatcher;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * json请求处理
 */
public class JsonBodyMappingSearcher extends AnnotationBeanProcessor implements MetadataHandler {

  static final List<Class<? extends Annotation>> CONTROLLERS = Arrays.asList(Controller.class, RestController.class);

  static final MappingAnnotationResolver MAPPING_ANNOTATION_RESOLVER = new MappingAnnotationResolver();

  /**
   * 映射的请求
   */
  private Map<OrRequestMatcher, AnnotationMetadata> apis = new LinkedHashMap<>();

  public JsonBodyMappingSearcher() {
    setResolver(new AnnotationResolverImpl(JsonBodyRequest.class));
    setMetadataHandler(this);
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (!ReflectUtils.isAnyAnnotationPresent(AopUtils.getTargetClass(bean), CONTROLLERS)) {
      return bean;
    }
    return super.postProcessAfterInitialization(bean, beanName);
  }

  @Override
  public void handle(List<AnnotationMetadata> list) {
    list.forEach(am -> {
      MappingAnnotationMetadata metadata = (MappingAnnotationMetadata) MAPPING_ANNOTATION_RESOLVER.resolveMetadata(am.getBean(), am.getMethod());
      metadata.getApiDescriptors().forEach(ad -> {
        LinkedList<RequestMatcher> matchers = new LinkedList<>();
        ad.getPaths().forEach(path -> ad.getMethods().forEach(rm -> matchers.add(new AntPathRequestMatcher(path, rm))));
        apis.put(new OrRequestMatcher(matchers), am);
      });
    });
  }

  public Map<OrRequestMatcher, AnnotationMetadata> getApis() {
    return apis;
  }
}
