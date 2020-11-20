package com.benefitj.spring.influxdb.enable;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * 引用 InfluxWriteManagerConfiguration
 */
@Lazy
@Import(InfluxWriteManagerConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableInfluxWriteManager {
}
