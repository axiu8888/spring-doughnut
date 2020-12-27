package com.benefitj.spring.security.url;

import java.lang.annotation.*;

/**
 * URL需要认证的注解
 *
 * @author DINGXIUAN
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface UrlAuthenticated {
}
