package com.benefitj.spring.mvc.mapping;

import com.benefitj.core.DUtils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.core.Regex;
import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.MetadataHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RequestMapping 查找
 */
public class MappingAnnotationBeanProcessor extends AnnotationBeanProcessor implements MetadataHandler {

  static final List<Class<? extends Annotation>> CONTROLLERS = Arrays.asList(Controller.class, RestController.class);

  public static final List<Class<? extends Annotation>> MAPPINGS = Arrays.asList(
      RequestMapping.class
      , GetMapping.class
      , PostMapping.class
      , PutMapping.class
      , DeleteMapping.class
      , PatchMapping.class
  );

  private final Regex regex = new Regex("\\$\\{(.*?)}");
  /**
   * 扫描的包路径
   */
  private final List<String> basePackages = new LinkedList<>();

  @Autowired
  private Environment environment;

  public MappingAnnotationBeanProcessor() {
    this.register(MAPPINGS);
    setMetadataHandler(this);
  }

  public MappingAnnotationBeanProcessor(Class<? extends Annotation> annotationType,
                                        MetadataHandler metadataHandler,
                                        List<String> basePackages) {
    super(annotationType, metadataHandler);
    this.basePackages.addAll(basePackages);
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    if (!(matchPackage(targetClass) && ReflectUtils.isAnyAnnotationPresent(targetClass, CONTROLLERS))) {
      return bean;
    }
    return super.postProcessAfterInitialization(bean, beanName);
  }

  @Override
  protected AnnotationMetadata resolveMetadata(Object bean, Method method) {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    MappingAnnotationMetadata metadata = new MappingAnnotationMetadata();
    metadata.setBean(bean);
    metadata.setTargetClass(targetClass);
    metadata.setMethod(method);
    metadata.setBaseMapping(AnnotationUtils.getAnnotation(targetClass, RequestMapping.class));
    metadata.setApi(AnnotationUtils.getAnnotation(targetClass, Api.class));
    metadata.getAnnotations().addAll((Collection) resolveAnnotations(method));
    return metadata;
  }

  @Override
  public void handle(List<AnnotationMetadata> list) {
    getMetadatas()
        .stream()
        .map(MappingAnnotationMetadata.class::cast)
        .forEach(am ->
            am.addDescriptors(am.getAnnotations()
                .stream()
                .map(annotation -> mapToApi(am, am.getBaseUrls(), annotation))
                .collect(Collectors.toList()))
        );
  }

  /**
   * 匹配包名
   *
   * @param targetClass 目标代理类
   * @return 返回匹配结果
   */
  protected boolean matchPackage(Class<?> targetClass) {
    String packageName = targetClass.getPackageName();
    return !(getBasePackages().isEmpty() || getBasePackages()
        .stream()
        .map(Regex::new)
        .allMatch(regex -> regex.find(packageName).isEmpty()));
  }

  public List<String> getBasePackages() {
    return basePackages;
  }

  public void addBasePackages(String... basePackages) {
    getBasePackages().addAll(Arrays.asList(basePackages));
  }

  protected ApiDescriptor mapToApi(MappingAnnotationMetadata metadata, String[] baseUrls, Annotation annotation) {
    if (annotation instanceof RequestMapping) {
      return mapToApi(metadata, baseUrls, ((RequestMapping) annotation).value(), ((RequestMapping) annotation).method());
    } else if (annotation instanceof GetMapping) {
      return mapToApi(metadata, baseUrls, ((GetMapping) annotation).value(), RequestMethod.GET);
    } else if (annotation instanceof PostMapping) {
      return mapToApi(metadata, baseUrls, ((PostMapping) annotation).value(), RequestMethod.POST);
    } else if (annotation instanceof PutMapping) {
      return mapToApi(metadata, baseUrls, ((PutMapping) annotation).value(), RequestMethod.PUT);
    } else if (annotation instanceof DeleteMapping) {
      return mapToApi(metadata, baseUrls, ((DeleteMapping) annotation).value(), RequestMethod.DELETE);
    } else if (annotation instanceof PatchMapping) {
      return mapToApi(metadata, baseUrls, ((PatchMapping) annotation).value(), RequestMethod.PATCH);
    }
    return null;
  }

  protected ApiDescriptor mapToApi(MappingAnnotationMetadata metadata, String[] baseUrls, String[] paths, RequestMethod... httpMethods) {
    ApiDescriptor descriptor = new ApiDescriptor();
    descriptor.setApiOperation(metadata.getMethod().getAnnotation(ApiOperation.class));
    descriptor.setHttpMethods(Stream.of(httpMethods).map(Enum::name).toArray(String[]::new));
    descriptor.getPaths().addAll(Stream.of(baseUrls)
        .map(br -> {
          List<String> strings = regex.find(br);
          if (strings.isEmpty()) {
            return br;
          }
          StringBuilder sb = new StringBuilder();
          int index = 0;
          for (String group : strings) {
            sb.append(br, 0, index = br.indexOf(group, index));
            String key = group.replace("${", "").replace(":}", "").replace("}", "");
            String value = environment.getProperty(key);
            if (value != null) {
              sb.append(value);
            }
            index += group.length();
          }
          if (sb.charAt(sb.length() - 1) == '/' && br.charAt(index) == '/') {
            index++;
          }
          sb.append(br, Math.min(index, br.length()), br.length());
          return sb.toString();
        })
        .map(baseUrl -> DUtils.isEndWiths(baseUrl, "/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl)
        .flatMap(baseUrl -> Stream.of(paths).map(path -> baseUrl + DUtils.startWiths(path, "/")))
        .collect(Collectors.toList()));
    return descriptor;
  }


}
