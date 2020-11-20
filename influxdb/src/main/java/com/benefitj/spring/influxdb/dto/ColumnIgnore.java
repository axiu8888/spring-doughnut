package com.benefitj.spring.influxdb.dto;

import java.lang.annotation.*;

/**
 * 忽略字段
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface ColumnIgnore {
}
