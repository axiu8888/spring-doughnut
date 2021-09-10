package com.benefitj.spring.swagger;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * swagger api
 */
@Import({SwaggerConfiguration.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface EnableSwaggerApi {
}
