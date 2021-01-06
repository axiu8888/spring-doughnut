package com.benefitj.spring.dynamicdatasource.aop;

import com.benefitj.spring.aop.AbstractAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * 通过AOP动态切换数据源
 */
@ConditionalOnMissingBean(DynamicDataSourceAspect.class)
@Aspect
public class DynamicDataSourceAspect extends AbstractAspect<DynamicDataSourcePointCutHandler> {

  /**
   * 切入点表达式
   */
  @Pointcut(
      "!execution(@com.benefitj.spring.aop.AopIgnore * *(..))" // 没有被AopIgnore注解注释
          + " && ("
          + " @annotation(org.springframework.stereotype.Service)"
          + " || @annotation(org.springframework.stereotype.Component)"
          + " || @annotation(com.benefitj.spring.dynamicdatasource.aop.DynamicDataSourceHandler)"
          + ")" // 被 @Component/@Service/@DynamicDataSourceHandler 注解注释
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
