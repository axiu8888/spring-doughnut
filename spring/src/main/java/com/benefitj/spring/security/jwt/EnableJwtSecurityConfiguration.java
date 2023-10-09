package com.benefitj.spring.security.jwt;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 引入 Web Security 配置
 */
@Import({
    JwtWebMvcConfig.class,
    JwtSecurityConfigurerAdapter.class,
})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableJwtSecurityConfiguration {
}
