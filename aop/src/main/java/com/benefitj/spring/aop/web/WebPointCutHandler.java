package com.benefitj.spring.aop.web;

import com.benefitj.spring.aop.PointCutHandler;

/**
 * AOP切入点处理：前置/后置/异常/返回
 */
public interface WebPointCutHandler extends PointCutHandler, WebRequestHolder {
}
