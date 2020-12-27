package com.benefitj.spring.security.url;

import java.lang.annotation.*;

/**
 * 请求路径的过滤器
 *
 * <p>
 * 根据方法上的注解匹配方法：
 * {@link org.springframework.web.bind.annotation.RequestMapping}
 * {@link org.springframework.web.bind.annotation.GetMapping}，GET
 * {@link org.springframework.web.bind.annotation.PostMapping}，POST
 * {@link org.springframework.web.bind.annotation.PutMapping}，PUT
 * {@link org.springframework.web.bind.annotation.DeleteMapping}，DELETE
 * {@link org.springframework.web.bind.annotation.PatchMapping}，PATCH
 *
 * @author DINGXIUAN
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface UrlPermitted {
}
