package com.benefitj.spring.security.url;

import com.benefitj.core.ReflectUtils;
import com.benefitj.spring.annotationprcoessor.AnnotationBeanProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 被 {@link UrlPermitted} 注解注释的接口不做认证
 *
 * @author DINGXIUAN
 */
public class AnnotationUrlRegistryConfigurerCustomizer extends AnnotationBeanProcessor implements UrlRegistryConfigurerCustomizer {

  protected static final Class<? extends Annotation>[] CONTROLLERS = new Class[]{Controller.class, RestController.class};

  private final List<UrlRegistryMetadata> allMetadata = Collections.synchronizedList(new LinkedList<>());

  public List<UrlRegistryMetadata> getAllMetadata() {
    return allMetadata;
  }

  @Override
  public void customize(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
    // all method permit
    register(registry, null);
    register(registry, MappingType.GET);
    register(registry, MappingType.POST);
    register(registry, MappingType.PUT);
    register(registry, MappingType.DELETE);
    register(registry, MappingType.PATCH);
  }

  public void register(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry, MappingType type) {
    // post method permit
    String[] antPatterns = getAllMetadata().stream()
        .filter(m -> match(m.getMethodTypes(), type))
        .flatMap(m -> m.getUris().stream())
        .distinct()
        .toArray(String[]::new);
    if (antPatterns.length > 0) {
      if (type != null) {
        registry.antMatchers(type.getHttpMethod(), antPatterns).permitAll();
      } else {
        registry.antMatchers(antPatterns).permitAll();
      }
    }
  }

  protected boolean match(List<MappingType> methods, MappingType... requires) {
    if (requires == null) {
      return true;
    }
    for (MappingType m : requires) {
      if (m == null) {
        return methods.isEmpty();
      } else {
        for (MappingType method : methods) {
          if (method == m) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    if (ReflectUtils.isAnnotationPresent(targetClass, CONTROLLERS, false)) {
      final boolean controllerPresent = targetClass.isAnnotationPresent(UrlPermitted.class);
      Collection<Method> methods = findMethods(targetClass, method -> {
        if (isHttpService(method)) {
          return controllerPresent
              ? !method.isAnnotationPresent(UrlAuthenticated.class)
              : method.isAnnotationPresent(UrlPermitted.class);
        }
        return false;
      });

      String[] controllerPath = getControllerPath(targetClass);
      List<UrlRegistryMetadata> metadatas = methods.stream()
          .map(method -> convertMetadata(method, controllerPath))
          .collect(Collectors.toList());

      if (!metadatas.isEmpty()) {
        this.allMetadata.addAll(metadatas);
      }
    }
    return bean;
  }

  protected String[] getControllerPath(Class<?> targetClass) {
    if (!targetClass.isAnnotationPresent(RequestMapping.class)) {
      return null;
    }
    String[] controllerPath = null;
    RequestMapping mapping = targetClass.getAnnotation(RequestMapping.class);
    if (!StringUtils.isAllBlank(mapping.value())) {
      controllerPath = mapping.value();
    } else if (!StringUtils.isAllBlank(mapping.path())) {
      controllerPath = mapping.path();
    }
    return controllerPath;
  }

  protected UrlRegistryMetadata convertMetadata(Method method, String[] controllerPaths) {
    UrlRegistryMetadata metadata = new UrlRegistryMetadata();
    metadata.setMethod(method);
    MappingType type = MappingType.of(method);
    if (type != null) {
      switch (type) {
        case REQUEST:
          RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
          setMetatdata(metadata, controllerPaths, requestMapping.value(), requestMapping.path(), requestMapping.method());
          break;
        case POST:
          PostMapping postMapping = method.getAnnotation(PostMapping.class);
          setMetatdata(metadata, controllerPaths, postMapping.value(), postMapping.path(), type.getRequestMethod());
          break;
        case GET:
          GetMapping getMapping = method.getAnnotation(GetMapping.class);
          setMetatdata(metadata, controllerPaths, getMapping.value(), getMapping.path(), type.getRequestMethod());
          break;
        case PUT:
          PutMapping putMapping = method.getAnnotation(PutMapping.class);
          setMetatdata(metadata, controllerPaths, putMapping.value(), putMapping.path(), type.getRequestMethod());
          break;
        case DELETE:
          DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
          setMetatdata(metadata, controllerPaths, deleteMapping.value(), deleteMapping.path(), type.getRequestMethod());
          break;
        case PATCH:
          PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
          setMetatdata(metadata, controllerPaths, patchMapping.value(), patchMapping.path(), type.getRequestMethod());
          break;
      }
    }
    return metadata;
  }

  protected void setMetatdata(UrlRegistryMetadata metadata,
                              String[] controllerPaths,
                              String[] value,
                              String[] path,
                              RequestMethod... methods) {
    if (methods.length > 0) {
      metadata.setMethodTypes(Arrays.stream(methods)
          .map(rm -> MappingType.valueOf(rm.name()))
          .collect(Collectors.toList()));
    }

    String[] array;
    if (!StringUtils.isAllBlank(value)) {
      array = value;
    } else if (!StringUtils.isAllBlank(path)) {
      array = path;
    } else {
      array = new String[0];
    }
    List<String> uris;
    if (array.length == 0) {
      uris = controllerPaths != null ? Arrays.asList(controllerPaths) : new LinkedList<>();
    } else {
      uris = Stream.of(array)
          .flatMap(p -> controllerPaths != null && controllerPaths.length > 0
              ? Stream.of(controllerPaths).map(prefix -> jointUrl(prefix, p))
              : Stream.of(p))
          .collect(Collectors.toList());
    }
    metadata.setUris(uris);
  }

  protected String jointUrl(String prefix, String path) {
    if (StringUtils.isBlank(path)) {
      return prefix;
    }
    if (prefix.endsWith("/")) {
      return path.startsWith("/")
          ? (prefix + path.replaceFirst("/", ""))
          : (prefix + path);
    }
    return path.startsWith("/") ? prefix + path : prefix + "/" + path;
  }

  protected boolean isHttpService(Method method) {
    if (Modifier.isStatic(method.getModifiers())) {
      return false;
    }
    if (!Modifier.isPublic(method.getModifiers())) {
      return false;
    }
    return MappingType.match(method);
  }

  public static class UrlRegistryMetadata {

    /**
     * bean实例
     */
    private Object bean;
    /**
     * 目标类
     */
    private Class<?> targetClass;
    /**
     * 方法
     */
    private Method method;
    /**
     * 路径
     */
    private List<String> uris = Collections.emptyList();
    /**
     * 支持的方法
     */
    private List<MappingType> methodTypes = Collections.emptyList();

    public UrlRegistryMetadata() {
    }

    public UrlRegistryMetadata(Object bean, Class<?> targetClass, Method method) {
      this.bean = bean;
      this.targetClass = targetClass;
      this.method = method;
    }

    public Object getBean() {
      return bean;
    }

    public void setBean(Object bean) {
      this.bean = bean;
    }

    public Class<?> getTargetClass() {
      return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
      this.targetClass = targetClass;
    }

    public Method getMethod() {
      return method;
    }

    public void setMethod(Method method) {
      this.method = method;
    }

    public List<String> getUris() {
      return uris;
    }

    public void setUris(List<String> uris) {
      this.uris = uris;
    }

    public List<MappingType> getMethodTypes() {
      return methodTypes;
    }

    public void setMethodTypes(List<MappingType> methodTypes) {
      this.methodTypes = methodTypes;
    }
  }

}
