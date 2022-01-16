package com.benefitj.scaffold.quartz;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 引入 quartz 配置
 */
@Import({QuartzConfiguration.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableQuartzConfuration {
}
