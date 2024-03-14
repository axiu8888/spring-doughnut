package com.benefitj.spring.aop.web;

import com.benefitj.spring.aop.AdviceImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.List;

/**
 * Web请求的切入点
 */
@ConditionalOnMissingBean(WebRequestAdvice.class)
@Aspect
public class WebRequestAdvice extends AdviceImpl {

  /**
   * 注册 Handler
   */
  @Autowired(required = false)
  public void register(List<WebPointCutHandler> handlers) {
    for (WebPointCutHandler handler : handlers) {
      super.register(handler);
    }
  }

  /**
   * 切入点表达式
   */
  @Pointcut("(@annotation(org.springframework.web.bind.annotation.RequestMapping)"
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
          + "(@within(com.benefitj.spring.aop.web.AopWebPointCut) && execution(public * *(..)))"// method
          + " || @annotation(com.benefitj.spring.aop.web.AopWebPointCut)"  // class
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
