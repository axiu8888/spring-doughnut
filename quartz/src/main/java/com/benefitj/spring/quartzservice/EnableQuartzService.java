package com.benefitj.spring.quartzservice;


import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * Quartz服务
 */
@Lazy
@Import({QuartzServiceConfiguration.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableQuartzService {
}

