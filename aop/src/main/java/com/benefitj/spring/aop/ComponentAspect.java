package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.List;

/**
 * spring组件的切入点
 */
@ConditionalOnMissingBean(ComponentAspect.class)
@Aspect
public class ComponentAspect extends AbstractAspect<ComponentPointCutHandler> {

  /**
   * 注册 Handler
   */
  @Autowired(required = false)
  @Override
  public void register(List<ComponentPointCutHandler> list) {
    super.register(list);
  }

  /**
   * 切入点表达式
   */
  @Pointcut(
      "!execution(@com.benefitj.spring.aop.AopIgnore * *(..))" // 没有被AopIgnore注解注释
          + " && ("
          + " @annotation(org.springframework.stereotype.Service)"
          + " || @annotation(org.springframework.stereotype.Component)"
          + " || @annotation(org.springframework.context.annotation.Configuration)"
          + ")" //注 @Component/@Service/@Configuration 解注释
          + " && ("
          + "(@within(com.benefitj.spring.aop.AopPointCut) && execution(public * *(..)))"// method
          + " || @annotation(com.benefitj.spring.aop.AopPointCut)"  // class
          + ")"
          + ")"
  )
  public void pointcut() {
    // ~
  }

  @Around("pointcut()")
  @Override
  public Object doAround(JoinPoint joinPoint) throws Throwable {
    return super.doAround(joinPoint);
  }

}
