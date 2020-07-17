package com.benefitj.spring.ctx;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * 初始化全局的Spring Context
 *
 * @author DINGXIUAN
 */
@Import({SpringCtxHolderInitializer.class})
@Lazy
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSpringCtxInit {
}
