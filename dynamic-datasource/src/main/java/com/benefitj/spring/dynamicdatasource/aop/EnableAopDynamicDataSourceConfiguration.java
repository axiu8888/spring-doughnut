package com.benefitj.spring.dynamicdatasource.aop;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * AOP数据源配置
 */
@Import({AopDynamicDataSourceConfiguration.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface EnableAopDynamicDataSourceConfiguration {
}
