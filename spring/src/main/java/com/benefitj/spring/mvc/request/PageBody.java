package com.benefitj.spring.mvc.request;

import java.lang.annotation.*;

/**
 * 分页请求
 *
 * @author DINGXIUAN
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Inherited
public @interface PageBody {
}
