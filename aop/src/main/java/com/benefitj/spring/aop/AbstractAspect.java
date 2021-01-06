package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 抽象的切入点
 */
public abstract class AbstractAspect<T extends PointCutHandler> {

  //   execution：用于匹配方法执行的连接点；
  //   within：用于匹配指定类型内的方法执行；
  //   this：用于匹配当前AOP代理对象类型的执行方法；注意是AOP代理对象的类型匹配，这样就可能包括引入接口也类型匹配；
  //   target：用于匹配当前目标对象类型的执行方法；注意是目标对象的类型匹配，这样就不包括引入接口也类型匹配；
  //   args：用于匹配当前执行的方法传入的参数为指定类型的执行方法；
  //   @within：用于匹配所以持有指定注解类型内的方法；
  //   @target：用于匹配当前目标对象类型的执行方法，其中目标对象持有指定的注解；
  //   @args：用于匹配当前执行的方法传入的参数持有指定注解的执行；
  //   @annotation：用于匹配当前执行方法持有指定注解的方法；
  //   bean：Spring AOP扩展的，AspectJ没有对于指示符，用于匹配特定名称的Bean对象的执行方法；
  //   reference pointcut：表示引用其他命名切入点，只有@ApectJ风格支持，Schema风格不支持。


  /**
   * 返回值缓存
   */
  private final ThreadLocal<Object> returnValueCache = new ThreadLocal<>();
  /**
   * 处理器
   */
  private final List<T> handlers = new CopyOnWriteArrayList<>();

  public AbstractAspect() {
  }

  /**
   * 注册 Handler
   */
  public void register(List<T> list) {
    if (list != null && !list.isEmpty()) {
      List<T> handlers = getHandlers();
      for (T h : list) {
        if (!handlers.contains(h)) {
          handlers.add(h);
        }
      }
    }
  }

  public List<T> getHandlers() {
    return handlers;
  }

  /**
   * 执行环绕通知
   *
   * @param joinPoint 切入点
   * @return 返回结果值
   * @throws Throwable 抛出的异常
   */
  public Object doAround(JoinPoint joinPoint) throws Throwable {
    final ProceedingJoinPoint pjp = (ProceedingJoinPoint) joinPoint;

    List<T> handlers = Collections.unmodifiableList(getHandlers());

    if (handlers.isEmpty()) {
      return onEmptyHandlerProcess(pjp);
    }

    Object returnValue = null;
    try {
      doBefore(pjp, handlers);
      returnValue = pjp.proceed(pjp.getArgs());
      setReturnValue(returnValue);
      doAfter(pjp, handlers);
    } catch (Throwable e) {
      doAfterThrowing(pjp, e, handlers);
      throw e;
    } finally {
      doAfterReturning(pjp, returnValue, handlers);
    }
    return getReturnValue();
  }

  /**
   * 空Handler时调用
   */
  public Object onEmptyHandlerProcess(ProceedingJoinPoint pjp) throws Throwable {
    return pjp.proceed(pjp.getArgs());
  }

  /**
   * 执行之前
   *
   * @param point    连接点
   * @param handlers 处理器
   */
  public void doBefore(ProceedingJoinPoint point, List<T> handlers) {
    handlers.forEach(h -> h.doBefore(point));
  }

  /**
   * 执行之后
   *
   * @param point    连接点
   * @param handlers 处理器
   */
  public void doAfter(ProceedingJoinPoint point, List<T> handlers) {
    handlers.forEach(h -> h.doAfter(point));
  }

  /**
   * 抛出异常时
   *
   * @param point    连接点
   * @param e        异常
   * @param handlers 处理器
   */
  public void doAfterThrowing(ProceedingJoinPoint point, Throwable e, List<T> handlers) {
    handlers.forEach(h -> h.doAfterThrowing(point, e));
  }

  /**
   * 执行之后，返回值时
   *
   * @param point       连接点
   * @param returnValue 返回值
   * @param handlers    处理器
   */
  public void doAfterReturning(ProceedingJoinPoint point, Object returnValue, List<T> handlers) {
    handlers.forEach(h -> h.doAfterReturning(point, returnValue));
  }

  public ThreadLocal<Object> getReturnValueCache() {
    return returnValueCache;
  }

  /**
   * 设置返回值
   *
   * @param value 返回值
   */
  public void setReturnValue(Object value) {
    getReturnValueCache().set(value);
  }

  /**
   * 获取返回值
   */
  public Object getReturnValue() {
    return getReturnValueCache().get();
  }

  public static Method checkProxy(Method methodArg, Object bean) {
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
            "WebRequestAspect method '%s' found on bean target class '%s', " +
                "but not found in any interface(s) for a bean JDK proxy. Either " +
                "pull the method up to an interface or switch to subclass (CGLIB) " +
                "proxies by setting proxy-target-class/proxyTargetClass " +
                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
      }
    }
    return method;
  }

}
