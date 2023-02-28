package com.benefitj.minio.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用MinIO
 */
@Import(MinioConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableMinio {
}
