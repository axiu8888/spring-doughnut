package com.benefitj.influxdb.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动注入InfluxDB的bean
 *
 * @author DINGXIUAN
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(InfluxConfiguration.class)
public @interface EnableInflux {
}
