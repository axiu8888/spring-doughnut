package com.benefitj.spring.quartz.caller;

import org.quartz.DisallowConcurrentExecution;

/**
 * 不允许并发执行
 */
@DisallowConcurrentExecution
public class DisallowConcurrentJobTaskCaller extends SimpleJobTaskCaller {
}
