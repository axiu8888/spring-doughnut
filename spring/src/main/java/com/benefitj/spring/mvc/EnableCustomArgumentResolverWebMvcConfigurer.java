package com.benefitj.spring.mvc;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自定义参数解析
 *
 * @author DINGXIUAN
 */
@Import({CustomArgumentResolverWebMvcConfigurer.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EnableCustomArgumentResolverWebMvcConfigurer {
}
