package com.benefitj.spring.influxdb.spring;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * 引用 InfluxWriteManagerConfiguration
 *
 * @author DINGXIUAN
 */
@Lazy
@Import(InfluxWriteManagerConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableInfluxWriteManager {
}
