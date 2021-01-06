package com.benefitj.spring.dynamicdatasource.aop;

import java.lang.annotation.*;

/**
 * 动态数据源代理
 *
 * @author DINGXIUAN
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DynamicDataSourceHandler {
}
