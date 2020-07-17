package com.benefitj.spring.ctx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Spring Context
 */
public class SpringCtxHolder {

  public static SpringCtxHolder getInstance() {
    return Holder.INSTANCE;
  }

  public static ApplicationContext getCtx() {
    return getInstance().getContext();
  }

  private static final class Holder {

    private static final SpringCtxHolder INSTANCE;

    static {
      INSTANCE = new SpringCtxHolder();
    }

  }

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Spring的ApplicationContext对象
   */
  private volatile ApplicationContext context;
  /**
   * Spring是否已销毁
   */
  private volatile boolean destroy = false;

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
      logger.warn("applicationContext已被销毁");
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

}
