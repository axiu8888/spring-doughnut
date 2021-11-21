package com.benefitj.spring.influxdb.spring;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * influxdb自动缓存道文件并上传
 *
 * @author DINGXIUAN
 */
@Lazy
@Import(InfluxWriterManagerConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableInfluxWriterManager {
}
