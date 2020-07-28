package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Web请求的切入点
 */
@ConditionalOnMissingBean(WebRequestAspect.class)
@Aspect
public class WebRequestAspect {
  /**
   * 处理器
   */
  private final List<WebPointCutHandler> handlers = new CopyOnWriteArrayList<>();
  /**
   * 返回值缓存
   */
  private final ThreadLocal<Object> returnValueCache = new ThreadLocal<>();

  public WebRequestAspect() {
  }

  /**
   * 注册 Handler
   */
  @Autowired(required = false)
  public void register(List<WebPointCutHandler> list) {
    if (list != null && !list.isEmpty()) {
      for (WebPointCutHandler h : list) {
        if (h != null && !handlers.contains(h)) {
          handlers.add(h);
        }
      }
    }
  }

  public List<WebPointCutHandler> getHandlers() {
    return handlers;
  }

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
   * 切入点表达式
   */
  @Pointcut(
      "!execution(@com.benefitj.spring.aop.AopIgnore * *(..))" // 没有被AopIgnore注解注释
          + " && ("
          + " (@annotation(org.springframework.web.bind.annotation.RequestMapping)"
          + " || @annotation(org.springframework.web.bind.annotation.RestController)"
          + " || @annotation(org.springframework.web.bind.annotation.GetMapping)"
          + " || @annotation(org.springframework.web.bind.annotation.PostMapping)"
          + " || @annotation(org.springframework.web.bind.annotation.DeleteMapping)"
          + " || @annotation(org.springframework.web.bind.annotation.PutMapping)"
          + " || @annotation(org.springframework.web.bind.annotation.PatchMapping)"
          + " || @annotation(org.springframework.stereotype.Controller)"
          + " || @annotation(org.springframework.web.bind.annotation.Mapping)"
          + ")" // 被springMVC注解注释
          + " && ("
          + "(@within(com.benefitj.spring.aop.AopWebPointCut) && execution(public * *(..)))"// method
          + " || @annotation(com.benefitj.spring.aop.AopWebPointCut)"  // class
          + ")"
          + ")"
  )
  public void pointcut() {
    // ~
  }

  @Around("pointcut()")
  public Object doAround(JoinPoint joinPoint) throws Throwable {
    final ProceedingJoinPoint pjp = (ProceedingJoinPoint) joinPoint;

    List<WebPointCutHandler> handlers = getHandlers();

    if (handlers.isEmpty()) {
      return onEmptyHandlerProcess(pjp);
    }

    Object returnValue = null;
    try {
      doBefore(pjp, handlers);
      returnValue = pjp.proceed(pjp.getArgs());
      setReturnValueCache(returnValue);
      doAfter(pjp, handlers);
    } catch (Throwable e) {
      doAfterThrowing(pjp, e, handlers);
      throw e;
    } finally {
      doAfterReturning(pjp, returnValue, handlers);
    }
    return getReturnValueCache();
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
  public void doBefore(ProceedingJoinPoint point, List<WebPointCutHandler> handlers) {
    handlers.forEach(h -> h.doBefore(point));
  }

  /**
   * 执行之后
   *
   * @param point    连接点
   * @param handlers 处理器
   */
  public void doAfter(ProceedingJoinPoint point, List<WebPointCutHandler> handlers) {
    handlers.forEach(h -> h.doAfter(point));
  }

  /**
   * 抛出异常时
   *
   * @param point    连接点
   * @param e        异常
   * @param handlers 处理器
   */
  public void doAfterThrowing(ProceedingJoinPoint point, Throwable e, List<WebPointCutHandler> handlers) {
    handlers.forEach(h -> h.doAfterThrowing(point, e));
  }

  /**
   * 执行之后，返回值时
   *
   * @param point       连接点
   * @param returnValue 返回值
   * @param handlers    处理器
   */
  public void doAfterReturning(ProceedingJoinPoint point, Object returnValue, List<WebPointCutHandler> handlers) {
    handlers.forEach(h -> h.doAfterReturning(point, returnValue));
  }

  /**
   * 设置返回值
   *
   * @param value 返回值
   */
  public void setReturnValueCache(Object value) {
    returnValueCache.set(value);
  }

  /**
   * 获取返回值
   */
  public Object getReturnValueCache() {
    return returnValueCache.get();
  }

}
