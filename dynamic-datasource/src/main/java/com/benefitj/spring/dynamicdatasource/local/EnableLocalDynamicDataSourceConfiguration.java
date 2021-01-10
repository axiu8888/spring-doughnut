package com.benefitj.spring.dynamicdatasource.local;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 本地缓存数据源配置
 */
@Import({LocalDynamicDataSourceConfiguration.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface EnableLocalDynamicDataSourceConfiguration {
}
