package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 抽象的切入点
 */
public class AdviceImpl implements AopAdvice {

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
   * 处理器
   */
  private final List<PointCutHandler> handlers = new CopyOnWriteArrayList<>();

  public AdviceImpl() {
  }

  @Override
  public void register(PointCutHandler handler) {
    if (!getHandlers().contains(handler)) {
      this.getHandlers().add(handler);
    }
  }

  public List<PointCutHandler> getHandlers() {
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

    final List<PointCutHandler> handlers = getHandlers();
    if (handlers.isEmpty()) {
      return pjp.proceed(joinPoint.getArgs());
    }

    final AtomicReference<Object> returnValueRef = new AtomicReference<>();
    if (!isInterceptor(pjp, handlers, returnValueRef)) {
      try {
        doBefore(pjp, handlers);
        Object returnValue = pjp.proceed(pjp.getArgs());
        returnValueRef.set(returnValue);
        doAfter(pjp, returnValueRef, handlers);
      } catch (Throwable e) {
        doThrowing(pjp, e, handlers);
        throw e;
      } finally {
        doAfterReturning(pjp, handlers);
      }
    }
    return returnValueRef.get();
  }

}
