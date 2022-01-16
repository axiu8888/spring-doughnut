package com.benefitj.scaffold.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 引入 Web Security 配置
 */
@Import({
    WebMvcConfig.class,
    DefaultWebSecurityConfigurerAdapter.class,
    SystemFileManagerConfiguration.class,
})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableScaffoldWebSecurityConfiguration {
}
