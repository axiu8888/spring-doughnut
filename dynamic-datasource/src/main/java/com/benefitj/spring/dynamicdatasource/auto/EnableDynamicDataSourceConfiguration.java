package com.benefitj.spring.dynamicdatasource.auto;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 多数据源配置
 */
@Import({DynamicDataSourceConfiguration.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface EnableDynamicDataSourceConfiguration {
}
