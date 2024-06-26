package com.benefitj.spring.ctx;

import com.benefitj.core.NetworkUtils;
import com.benefitj.core.SingletonSupplier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Spring Context
 */
public class SpringCtxHolder {

  static final SingletonSupplier<SpringCtxHolder> singleton = SingletonSupplier.of(SpringCtxHolder::new);

  static SpringCtxHolder get() {
    return singleton.get();
  }

  public static ApplicationContext getCtx() {
    return get().getContext();
  }

  private final Logger log = LoggerFactory.getLogger(getClass());


  public static final String PLACEHOLER_REGEX = "^\\$\\{.*\\}$|^#\\{.*\\}$";

  /**
   * Spring的ApplicationContext对象
   */
  volatile ApplicationContext context;
  /**
   * Spring是否已销毁
   */
  volatile boolean destroy = false;

  private SpringCtxHolder() {
    // ~
  }

  /**
   * 获取ApplicationContext对象
   */
  public ApplicationContext getContext() {
    if (context == null) {
      throw new IllegalArgumentException("applicationContext属性未注入, 请在ApplicationContextAware中注入.");
    }
    if (isDestroy()) {
      log.warn("applicationContext已被销毁");
    }
    return context;
  }

  public boolean isDestroy() {
    return destroy;
  }

  public void setDestroy(boolean destroy) {
    this.destroy = destroy;
  }

  /**
   * 设置ApplicationContext对象
   *
   * @param context 上下文对象
   */
  protected void setContext(ApplicationContext context) {
    this.context = context;
  }

  /**
   * 加载class
   *
   * @param className 全类名
   * @param <T>       类
   * @return 返回加载的类
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> loadClass(String className) {
    try {
      ClassLoader classLoader = getCtx().getClassLoader();
      return (Class<T>) classLoader.loadClass(className);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 是否包含 Bean
   */
  public static boolean containsBean(String bean) {
    return getCtx().containsBean(bean);
  }

  /**
   * 获取Bean, 自动转型为所赋值对象的类型.
   *
   * @param name Bean名
   * @param <T>  类型
   * @return 返回获取的Bean
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) {
    return (T) getCtx().getBean(name);
  }

  /**
   * 获取Bean, 自动转型为所赋值对象的类型.
   *
   * @param requiredType 要求的类型
   * @param <T>          类型
   * @return 返回获取的Bean
   */
  public static <T> T getBean(Class<T> requiredType) {
    return getCtx().getBean(requiredType);
  }

  /**
   * 获取某类型的bean
   *
   * @param name Bean名
   * @param type Class
   * @param <T>  类型
   * @return 返回类型的集合
   */
  public static <T> T getBean(String name, Class<T> type) {
    return getCtx().getBean(name, type);
  }

  /**
   * 获取某类型的bean
   *
   * @param type Class
   * @param <T>  类型
   * @return 返回类型的集合
   */
  public static <T> Map<String, T> getBeansOfType(Class<T> type) {
    return getCtx().getBeansOfType(type);
  }

  /**
   * 获取某类型的bean
   *
   * @param type                 Class
   * @param includeNonSingletons -
   * @param allowEagerInit       -
   * @param <T>                  类型
   * @return 返回类型的集合
   */
  public static <T> Map<String, T> getBeansOfType(Class<T> type,
                                                  boolean includeNonSingletons,
                                                  boolean allowEagerInit) {
    return getCtx().getBeansOfType(type, includeNonSingletons, allowEagerInit);
  }

  /**
   * 获取环境参数
   */
  public static Environment getEnvironment() {
    return getCtx().getEnvironment();
  }

  /**
   * 获取环境参数
   *
   * @param key 键
   * @return 返回对应的值
   */
  public static String getEnvProperty(String key) {
    return getEnvProperty(key, "");
  }

  /**
   * 获取环境参数
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 返回对应的值
   */
  public static String getEnvProperty(String key, String defaultValue) {
    String newKey = key.trim();
    if (matchPlaceHolder(newKey)) {
      newKey = newKey.substring(2, newKey.length() - 1);
      if (newKey.contains("@environment[")) {
        int startAt = newKey.indexOf("'", newKey.indexOf("@environment[")) + 1;
        int endAt = newKey.indexOf("'", startAt);
        String placeholder = newKey.substring(startAt, endAt);
        String subValue = getEnvironment().getProperty(placeholder);
        if (StringUtils.isBlank(subValue) && (newKey.indexOf("?:", endAt) > 0)) {
          String value = newKey.substring(newKey.indexOf("?:", endAt) + 2).trim();
          if (StringUtils.isNotBlank(value)) {
            value = value.startsWith("'") ? value.substring(1) : value;
            value = value.endsWith("'") ? value.substring(0, value.length() - 1) : value;
            return value;
          }
        }
        return StringUtils.isNotBlank(subValue) ? subValue : defaultValue;
      }
    }
    return getEnvironment().getProperty(newKey, defaultValue);
  }

  /**
   * 获取App名称
   */
  public static String getAppName() {
    return getEnvProperty("spring.application.name", "");
  }

  /**
   * 获取服务器端口
   */
  public static String getServerPort() {
    return getEnvProperty("server.port", "8080");
  }

  /**
   * 获取服务器端口
   */
  public static String getBaseUrl() {
    String ip = NetworkUtils.getLocalHost().getHostAddress();
    String port = SpringCtxHolder.getServerPort();
    String path = SpringCtxHolder.getServerContextPath();
    return "http://" + ip + ":" + port + path;
  }

  /**
   * 获取服务器上下文地址
   */
  public static String getServerContextPath() {
    return getEnvProperty("server.servlet.context-path", "");
  }

  /**
   * 是否匹配差值规则
   */
  public static boolean matchPlaceHolder(String key) {
    return key.matches(PLACEHOLER_REGEX);
  }

}
