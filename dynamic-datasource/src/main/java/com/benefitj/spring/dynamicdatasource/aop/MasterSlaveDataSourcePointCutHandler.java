package com.benefitj.spring.dynamicdatasource.aop;

import com.benefitj.spring.dynamicdatasource.LookupKeyContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 主从数据源切换
 */
public class MasterSlaveDataSourcePointCutHandler implements DataSourcePointCutHandler, InitializingBean {

  public static final Collection<String> DEFAULT_SLAVE_METHODS;
  public static final Collection<String> DEFAULT_MASTER_METHODS;

  static {
    DEFAULT_SLAVE_METHODS = Collections.unmodifiableCollection(Arrays.asList("get", "find", "select", "query"));
    DEFAULT_MASTER_METHODS = Collections.unmodifiableCollection(Arrays.asList("insert", "delete", "update", "save", "write"));
  }

  /**
   * 查询操作的方法前缀，get/select/find/query
   */
  private Set<String> slaveMethods = new HashSet<>();
  /**
   * 写入操作的方法前缀，save/insert/write
   */
  private Set<String> masterMethods = new HashSet<>();
  /**
   * 查找数据源的上下文
   */
  private LookupKeyContext lookupKeyContext;

  private final Map<Method, ServiceMethod> serviceMethods = new ConcurrentHashMap<>();
  /**
   * 是否使用默认的方法前缀
   */
  private boolean useDefaultMethod = true;

  @Override
  public void afterPropertiesSet() throws Exception {
    if (isUseDefaultMethod()) {
      this.getSlaveMethods().addAll(DEFAULT_SLAVE_METHODS);
      this.getMasterMethods().addAll(DEFAULT_MASTER_METHODS);
    }
  }

  @Override
  public void doBefore(JoinPoint joinPoint) {
    Method method = checkProxy(((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getTarget());
    ServiceMethod serviceMethod = getServiceMethods().computeIfAbsent(method, this::parseServiceMethod);
    if (serviceMethod.isSupport()) {
      getLookupKeyContext().set(serviceMethod.getType());
    }
  }

  @Override
  public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
    Method method = checkProxy(((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getTarget());
    ServiceMethod serviceMethod = getServiceMethods().get(method);
    if (serviceMethod.isSupport()) {
      getLookupKeyContext().remove();
    }
  }

  public ServiceMethod parseServiceMethod(Method method) {
    DataSourceHandler handler = null;
    if (method.isAnnotationPresent(DataSourceHandler.class)) {
      handler = method.getDeclaredAnnotation(DataSourceHandler.class);
    }

    DataSourceHandler classHandler = null;
    Class<?> declaringClass = method.getDeclaringClass();
    if (declaringClass.isAnnotationPresent(DataSourceHandler.class)) {
      classHandler = declaringClass.getDeclaredAnnotation(DataSourceHandler.class);
    }

    ServiceMethod sm = new ServiceMethod(method);
    sm.setSupport(handler != null);
    if (sm.isSupport()) {
      Set<String> slavePrefixSet = new HashSet<>(getSlaveMethods());
      if (classHandler != null) {
        slavePrefixSet.addAll(Stream.of(classHandler.slave())
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toSet()));
      }
      boolean slaveFlag = slavePrefixSet.stream().anyMatch(
          prefix -> sm.getMethodName().startsWith(prefix));
      if (slaveFlag) {
        sm.setType(MethodType.SLAVE);
      } else {
        Set<String> masterPrefixSet = new HashSet<>(getMasterMethods());
        if (classHandler != null) {
          masterPrefixSet.addAll(Stream.of(classHandler.master())
              .filter(StringUtils::isNotBlank)
              .collect(Collectors.toSet()));
        }
        boolean masterFlag = masterPrefixSet.stream().anyMatch(
            prefix -> sm.getMethodName().startsWith(prefix));
        if (masterFlag) {
          sm.setType(MethodType.MASTER);
        } else {
          sm.setType(MethodType.CUSTOM);
        }
      }
    }
    return sm;
  }

  public LookupKeyContext getLookupKeyContext() {
    return lookupKeyContext;
  }

  public void setLookupKeyContext(LookupKeyContext lookupKeyContext) {
    this.lookupKeyContext = lookupKeyContext;
  }

  public Map<Method, ServiceMethod> getServiceMethods() {
    return serviceMethods;
  }

  public Set<String> getSlaveMethods() {
    return slaveMethods;
  }

  public void setSlaveMethods(Set<String> slaveMethods) {
    this.slaveMethods = slaveMethods;
  }

  public Set<String> getMasterMethods() {
    return masterMethods;
  }

  public void setMasterMethods(Set<String> masterMethods) {
    this.masterMethods = masterMethods;
  }

  public boolean isUseDefaultMethod() {
    return useDefaultMethod;
  }

  public void setUseDefaultMethod(boolean useDefaultMethod) {
    this.useDefaultMethod = useDefaultMethod;
  }

}
