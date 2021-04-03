package com.benefitj.spring.quartz.enbale;


import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * Quartz服务
 */
@Lazy
@Import({QuartzMethodServiceConfiguration.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableQuartzMethodService {
}

