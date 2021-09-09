package com.benefitj.spring.ctx;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 初始化全局的Spring Context
 *
 * @author DINGXIUAN
 */
@Import({SpringCtxHolderInitializer.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSpringCtxInit {
}
