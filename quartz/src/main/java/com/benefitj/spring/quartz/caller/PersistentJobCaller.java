package com.benefitj.spring.quartz.caller;


import org.quartz.PersistJobDataAfterExecution;

/**
 * 执行后持久化数据
 */
@PersistJobDataAfterExecution
public class PersistentJobCaller extends DefaultJobCaller {
}

